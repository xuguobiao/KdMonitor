package com.kido.sdk.kdmonitor.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KdHandleThreadPool {

    private final static int POOL_SIZE = 1;
    private final static int MAX_POOL_SIZE = 1;
    private final static int KEEP_ALIVE_TIME = 4;
    private final Executor mExecutor;
    private final static String THREAD_NAME = "kdmonitor-stat-thread-pool";

    public KdHandleThreadPool() {

        ThreadFactory factory = new PriorityThreadFactory(THREAD_NAME, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
        mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
    }

    public void execute(Runnable command) {
        mExecutor.execute(command);
    }

    public Executor getExecutor() {
        return mExecutor;
    }
}
