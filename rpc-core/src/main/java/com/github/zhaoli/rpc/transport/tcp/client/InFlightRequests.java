/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zhaoli.rpc.transport.tcp.client;

import com.github.zhaoli.rpc.common.domain.ResponseFuture;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;

public class InFlightRequests implements Closeable {
    private final static long TIMEOUT_SEC = 10L;
    private final Semaphore semaphore = new Semaphore(100);
    private final Map<String, ResponseFuture> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;
    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    public void put(ResponseFuture responseFuture) throws InterruptedException, TimeoutException {
        if(semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
        } else {
            throw new TimeoutException();
        }
    }

    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if( System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1000000000L) {
                semaphore.release();
                return true;
            } else {
                return false;
            }
        });
    }

    public ResponseFuture remove(String requestId) {
        ResponseFuture future = futureMap.remove(requestId);
        if(null != future) {
            semaphore.release();
        }
        return future;
    }

    @Override
    public void close() {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
