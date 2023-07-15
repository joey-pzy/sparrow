package cn.kavier.demo.cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    public static void main(String[] args) throws InterruptedException {
        Search search = new Search();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long s = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1; i++) {
            executorService.submit(()->{
                search.search2();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        System.out.println("执行时间："+(System.currentTimeMillis() - s));

        executorService.shutdown();
    }


}
