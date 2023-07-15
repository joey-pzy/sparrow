package cn.kavier.demo.cache;

import java.util.concurrent.CountDownLatch;

public class MyCacheCountDownLatch extends CountDownLatch {
    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public MyCacheCountDownLatch(int count) {
        super(count);
    }

    @Override
    public void await() throws InterruptedException {
        super.await();
    }
}
