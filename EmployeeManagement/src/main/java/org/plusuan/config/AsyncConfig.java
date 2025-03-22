package org.plusuan.config;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;


public class AsyncConfig {

    private final ExecutorService executor;

    public AsyncConfig() {
        int corePoolSize = 3;
        int maxPoolSize = 6;
        long keepAliveTime = 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        int queueCapacity = 100;

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(0);
            private final String prefix = "asyncExecutor-";

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(prefix + count.getAndIncrement());
                return t;
            }
        };

        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                timeUnit,
                new LinkedBlockingQueue<>(queueCapacity),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
