package com.github.zhaoli.rpc.registry.curator;

import com.github.zhaoli.rpc.common.constant.CharsetConst;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.registry.api.ServiceURLAddOrUpdateCallback;
import com.github.zhaoli.rpc.registry.api.ServiceURLRemovalCallback;
import com.github.zhaoli.rpc.registry.api.support.AbstractServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class CuratorServiceRegistry extends AbstractServiceRegistry {

    private static final String ZK_REGISTRY_PATH = "/easy";

    private CuratorFramework curatorFramework;

    @Override
    public void init() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,
                        10)).build();
        curatorFramework.start();
    }

    @Override
    public void discover(String interfaceName, ServiceURLRemovalCallback serviceURLRemovalCallback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        String path = generatePath(interfaceName);
        try {
            log.info("discovering...");
            List<String> addresses = curatorFramework.getChildren().usingWatcher(new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                        discover(interfaceName, serviceURLRemovalCallback, serviceURLAddOrUpdateCallback);
                    }
                }
            }).forPath(path);

            log.info("interfaceName:{} -> addresses:{}", interfaceName, addresses);
            List<ServiceURL> dataList = new ArrayList<>();
            for (String node : addresses) {
                dataList.add(watchService(interfaceName, node, serviceURLAddOrUpdateCallback));
            }
            log.info("node data: {}", dataList);
            serviceURLRemovalCallback.removeNotExisted(dataList);
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.REGISTRY_ERROR,"ZK故障", e);
        }
    }

    private ServiceURL watchService(String interfaceName, String address, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        String path = generatePath(interfaceName) + "/" + address;
        try {
            byte[] bytes = curatorFramework.getData().usingWatcher(new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                        watchService(interfaceName, address, serviceURLAddOrUpdateCallback);
                    }
                }
            }).forPath(path);
            ServiceURL serviceURL = ServiceURL.parse(new String(bytes, CharsetConst.UTF_8));
            serviceURLAddOrUpdateCallback.addOrUpdate(serviceURL);
            return serviceURL;
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.REGISTRY_ERROR,"ZK故障", e);
        }
    }


    @Override
    public void register(String address, String interfaceName) {
        String path = generatePath(interfaceName);
        try {
            if(curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(path, new byte[0]);
            }
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).
                    forPath(path + "/" + address, address.getBytes(CharsetConst.UTF_8));
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.REGISTRY_ERROR,"ZK故障", e);
        }
    }

    @Override
    public void close() {
        curatorFramework.close();
    }

    @Override
    public void unregister(String address, String interfaceName) {

    }
}
