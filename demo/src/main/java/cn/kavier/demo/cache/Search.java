package cn.kavier.demo.cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class Search {

    private String cache = null;

    private static ReentrantLock reentrantLock = new ReentrantLock();
    private volatile CountDownLatch cdl = null;

    public String search() {
        long s = System.currentTimeMillis();
        String c = cache();
        if (c != null) {
            System.out.println("hit cache");
            return c;
        } else {
            synchronized (this) {
                String c1 = cache();
                if (c1 != null) {
                    System.out.println("lock and hit cache");
                    return c1;
                }
                String db = db();
                System.out.println("lock and hit db");
                setCache(db);
                return db;
            }
        }
    }

    public String search2() {
        String c = cache();
        if (c != null) {
            System.out.println(Thread.currentThread().getId() + "  hit cache "+c);
            return c;
        } else {
            if (reentrantLock.tryLock()) {
                cdl = new CountDownLatch(1);
                String db = db();
                System.out.println("lock and hit db" + db);
                setCache(db);
                cdl.countDown();
                return db;
            } else {
                while (true) {
                    try {
                        if (cdl != null) {
                            cdl.await();
                            String c1 = cache();
                            System.out.println("await and hit cache   " + c1);
                            return c1;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String searchWithSleep() {
        String c = cache();
        if (c != null) {
            System.out.println(Thread.currentThread().getId() + "  hit cache");
            return c;
        } else {
            try {
                if (reentrantLock.tryLock()) {
                    String db = db();
                    System.out.println("lock and hit db" + db);
                    setCache(db);
                    return db;
                } else {
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String c1 = cache();
                        System.out.println("sleep and hit cache=" + c1);
                        if (c1 != null) {
                            return c1;
                        }
                    }
                }
            } finally {
                if (reentrantLock.isLocked()) {
                    reentrantLock.unlock();
                }
            }
        }
    }

    private String cache() {
        return cache;
    }

    private void setCache(String cache) {
        this.cache = cache;
    }

    private String db() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return "db-data";
    }
}
