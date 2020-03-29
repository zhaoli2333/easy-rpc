package com.github.zhaoli.rpc.transport.tcp.server;

import com.github.zhaoli.rpc.transport.api.constant.FrameConstant;
import com.github.zhaoli.rpc.transport.api.support.AbstractServer;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpDecoder;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
@Slf4j
public class TcpNettyServer extends AbstractServer {
    private ChannelInitializer channelInitializer;
    private ChannelFuture channelFuture;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private EventExecutorGroup businessGroup;

    protected ChannelInitializer initPipeline() {
        // business group
        this.businessGroup = new UnorderedThreadPoolEventExecutor(this.getGlobalConfig().getProtocolConfig().getExecutor().getServer().getThreads(), new DefaultThreadFactory("business"));
        //metrics
        MetricsHandler metricsHandler = new MetricsHandler();
        return new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("metricHandler", metricsHandler)
                        .addLast("IdleStateHandler", new ServerIdleCheckHandler())
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameEncoder", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("ProtocolEncoder", new TcpEncoder())
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("ProtocolDecoder", new TcpDecoder())
                        .addLast(businessGroup, new TcpServerHandler(getGlobalConfig()));
            }
        };
    }

    @Override
    public void start() {
        this.channelInitializer = initPipeline();
        //两个事件循环器，第一个用于接收客户端连接，第二个用于处理客户端的读写请求
        //是线程组，持有一组线程
        log.info("支持EPOLL?:{}", Epoll.isAvailable());
        this.bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        this.workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        log.info("bossGroup:{},workerGroup:{},businessGroup:{}", bossGroup, workerGroup, businessGroup);
        try {
            //服务器辅助类，用于配置服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置服务器参数
            bootstrap.group(bossGroup, workerGroup)
                    //使用这种类型的NIO通道，现在是基于TCP协议的
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    //对Channel进行初始化，绑定实际的事件处理器，要么实现ChannelHandler接口，要么继承ChannelHandlerAdapter类
                    .childHandler(channelInitializer)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    //服务器配置项
                    //BACKLOG
                    //TCP维护有两个队列，分别称为A和B
                    //客户端发送SYN，服务器接收到后发送SYN ACK，将客户端放入到A队列
                    //客户端接收到后再次发送ACK，服务器接收到后将客户端从A队列移至B队列，服务器的accept返回。
                    //A和B队列长度之和为backlog
                    //当A和B队列长度之和大于backlog时，新的连接会被TCP内核拒绝
                    //注意：backlog对程序的连接数并无影响，影响的只是还没有被accept取出的连接数。
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //指定发送缓冲区大小
//                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    //指定接收缓冲区大小
//                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            //这里的option是针对于上面的NioServerSocketChannel
            //复杂的时候可能会设置多个Channel
            //.sync表示是一个同步阻塞执行，普通的Netty的IO操作都是异步执行的
            //一个ChannelFuture代表了一个还没有发生的I/O操作。这意味着任何一个请求操作都不会马上被执行
            //Netty强烈建议直接通过添加监听器的方式获取I/O结果，而不是通过同步等待(.sync)的方式
            //如果用户操作调用了sync或者await方法，会在对应的future对象上阻塞用户线程


            //绑定端口，开始监听
            //注意这里可以绑定多个端口，每个端口都针对某一种类型的数据（控制消息，数据消息）
            String host = InetAddress.getLocalHost().getHostAddress();
            this.channelFuture = bootstrap.bind(host, getGlobalConfig().getPort()).sync();
            //应用程序会一直等待，直到channel关闭
            log.info("服务器启动,当前服务器类型为:{}", this.getClass().getSimpleName());
        } catch (InterruptedException e) {
            log.error("error", e);
        } catch (UnknownHostException e) {
            log.error("error", e);
        }
    }

    @Override
    public void close() {
        getGlobalConfig().getRegistryConfig().close();
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if(businessGroup != null) {
            businessGroup.shutdownGracefully();
        }
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }

}
