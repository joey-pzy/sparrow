package cn.kavier.demo.fallback;

import java.util.HashMap;
import java.util.Map;

public class Main {

    static boolean result = false;

    static Map<String, Thread> cache = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("查询结果请求进来");

        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("外部回调成功");
            result = true;
            Thread checkThread = cache.get("1");
            checkThread.interrupt();
        }).start();

        int i = 0;
        while (true) {
            System.out.println("第"+ (++i) +"次查询支付结果");
            if (result) {
                System.out.println("支付成功");
                return;
            }
            cache.put("1", Thread.currentThread());
            try {
                System.out.println("睡第"+i+"次");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("支付成功");
                return;
            }
        }

    }
}
