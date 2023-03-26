package cn.kavier.demo.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class MyFTask implements Callable<Integer> {

    private final int m;
    private CountDownLatch countDownLatch;

    public MyFTask(int m, CountDownLatch countDownLatch) {
        this.m = m;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Integer call() throws Exception {
        Thread.sleep(this.m);
        countDownLatch.countDown();
        return this.m;
    }
}
