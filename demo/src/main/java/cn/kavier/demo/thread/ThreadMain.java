package cn.kavier.demo.thread;

import cn.kavier.demo.proxy.cglibProxy.MyController;

import java.util.concurrent.*;

public class ThreadMain {

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletionService completionService = new ExecutorCompletionService(executorService);

        CountDownLatch countDownLatch = new CountDownLatch(4);

        Future f1 = completionService.submit(new MyFTask(3000, countDownLatch));
        Future f2 = completionService.submit(new MyFTask(2000, countDownLatch));
        Future f3 = completionService.submit(new MyFTask(1000, countDownLatch));

        System.out.println("线程提交完");
        countDownLatch.await();
//        for (int i = 0; i < 3; i++) {
//            System.out.println(completionService.take().get());
//        }
        long start = System.currentTimeMillis();
        System.out.println(f1.isDone());
        System.out.println(f2.isDone());
        System.out.println(f3.isDone());
        long end = System.currentTimeMillis();

        synchronized (new MyController()) {
            System.out.println("结果收集完");
        }
        executorService.shutdown();
        System.out.println("执行完");
    }
}
