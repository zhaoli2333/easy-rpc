package com.github.zhaoli.rpc.transport.tcp.client;

import com.github.zhaoli.rpc.common.domain.*;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.enumeration.SerializerType;
import com.github.zhaoli.rpc.transport.api.constant.FrameConstant;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpDecoder;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpEncoder;
import com.github.zhaoli.rpc.transport.tcp.constant.TcpConstant;
import com.github.zhaoli.rpc.common.context.RPCThreadSharedContext;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.transport.api.support.AbstractClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
@Slf4j
public class TcpNettyClient extends AbstractClient {

    private static InFlightRequests inFlightRequests = new InFlightRequests();

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private volatile Channel futureChannel;
    private volatile boolean initialized = false;
    private volatile boolean destroyed = false;
    private volatile boolean closedFromOuter = false;
    private static ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();
    private ConnectRetryer connectRetryer = new ConnectRetryer();

    /**
     * 与Handler相关
     *
     * @return
     */
    protected ChannelInitializer initPipeline() {
        log.info("EasyRpcClient initPipeline...");
        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler", new ClientIdleCheckHandler())
                        // ByteBuf -> Message
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("EasyEncoder", new TcpEncoder())
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("EasyDecoder", new TcpDecoder())
                        //log
//                        .addLast("LoggingHandler",loggingHandler)
                        // keeplive
                        .addLast("KeepaliveHandler", keepaliveHandler)
                        .addLast("EasyClientHandler", new TcpClientHandler(TcpNettyClient.this));
            }
        };
    }


    @Override
    public boolean isAvailable() {
        return initialized && !destroyed;
    }

    @Override
    protected synchronized void connect() {
        if (initialized) {
            return;
        }
        this.group = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.TCP_NODELAY, true);
        try {
            doConnect();
        } catch (Exception e) {
            log.error("与服务器的连接出现故障", e);
            handleException(e);
        }
    }

    /**
     * 实现重新连接的重试策略
     * 不能使用guava retryer实现，因为它是阻塞重试，会占用IO线程
     */
    private void reconnect(){
        // 避免多次异常抛出，导致多次reconnect
        if (destroyed) {
            connectRetryer.run();
        }
    }

    private class ConnectRetryer implements Runnable {

        @Override
        public void run() {
            if (!closedFromOuter) {
                log.info("重新连接中...");
                try {
                    // 先把原来的连接关掉
                    if (futureChannel != null && futureChannel.isOpen()) {
                        futureChannel.close().sync();
                    }
                    doConnect();
                } catch (Exception e) {
                    log.info("重新连接失败，{} 秒后重试", TcpConstant.HEART_BEAT_TIME_OUT);
                    retryExecutor.schedule(connectRetryer, TcpConstant.HEART_BEAT_TIME_OUT, TimeUnit.SECONDS);
                }
            } else {
                log.info("ZK无法检测到该服务器，不再重试");
            }
        }
    }

    private synchronized void doConnect() throws InterruptedException {
        ChannelFuture future;
        String address = getServiceURL().getAddress();
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        future = bootstrap.connect(host, port).sync();
        this.futureChannel = future.channel();
        log.info("客户端已连接至 {}", address);
        log.info("客户端初始化完毕");
        initialized = true;
        destroyed = false;
    }

    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */
    @Override
    public void handleException(Throwable throwable) {
        // destroy设置为true，客户端提交请求后会立即被拒绝
        destroyed = true;
        log.error("", throwable);
        log.info("开始尝试重新连接...");
        try {
            reconnect();
        } catch (Exception e) {
            close();
            log.error("", e);
            throw new RPCException(ErrorEnum.CONNECT_TO_SERVER_FAILURE, "重试多次后仍然连接失败,关闭客户端,放弃重试");
        }
    }


    @Override
    public void handleRPCResponse(RPCResponse response) {
        ResponseFuture responseFuture = inFlightRequests.remove(response.getRequestId());
        if(responseFuture != null) {
            CompletableFuture<RPCResponse> future = responseFuture.getFuture();
            if(future != null) {
                future.complete(response);
            }
        }
    }

    /**
     * 提交请求
     *
     * @param request
     * @return
     */
    @Override
    public Future<RPCResponse> submit(RPCRequest request) {
        if (!initialized) {
            connect();
        }
        if (destroyed || closedFromOuter) {
            throw new RPCException(ErrorEnum.SUBMIT_AFTER_ENDPOINT_CLOSED, "当前Endpoint: {} 关闭后仍在提交任务", getServiceURL().getAddress());
        }
        log.info("客户端发起请求: {},请求的服务器为: {}", request, getServiceURL().getAddress());
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();

        // 将在途请求放到inFlightRequests中
        try {
            inFlightRequests.put(new ResponseFuture(request.getRequestId(), responseFuture));

            byte[] body = this.getSerializer().serialize(request);
            Header header = Header.builder()
                    .type(MessageType.REQUEST)
                    .version(1)
                    .serializerType(this.getSerializeType())
                    .build();

            Message message = Message.builder()
                    .header(header)
                    .body(body)
                    .build();

            this.futureChannel.writeAndFlush(message).addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
                @Override
                public void operationComplete(io.netty.util.concurrent.Future<? super Void> future) throws Exception {
                    if(!future.isSuccess()) {
                        responseFuture.completeExceptionally(future.cause());
                        futureChannel.close();
                    }
                }
            });
            log.info("请求已发送至{}", getServiceURL().getAddress());

        } catch (Exception e) {
            // 处理发送异常
            inFlightRequests.remove(request.getRequestId());
            responseFuture.completeExceptionally(e);
        }

        return responseFuture;
    }

    /**
     * 如果该Endpoint不提供任何服务，则将其关闭
     * 要做成幂等的，因为多个invoker都对应一个endpoint，当某个服务器下线时，可能会有多个interface（ClusterInvoker）
     * 都检测到地址变更，所以会关闭对应的invoker。
     */
    @Override
    public void close() {
        try {
            if (this.futureChannel != null && futureChannel.isOpen()) {
                this.futureChannel.close().sync();
            }
            destroyed = true;
            closedFromOuter = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (group != null && !group.isShuttingDown() && !group.isShutdown() && !group.isTerminated()) {
                group.shutdownGracefully();
            }
        }
    }
}
