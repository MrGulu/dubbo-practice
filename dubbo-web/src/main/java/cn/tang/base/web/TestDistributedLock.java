package cn.tang.base.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * @author tangwenlong
 * @description: 分布式锁的实现
 * @CreateDate: 2020-01-13
 */
@Slf4j
public class TestDistributedLock {

    /**
     * 使用zookeeper实现分布式锁，借助curator框架实现。
     * <p>
     * 四种锁方案：
     * InterProcessMutex：分布式可重入排它锁
     * InterProcessSemaphoreMutex：分布式排它锁
     * InterProcessReadWriteLock：分布式读写锁
     * InterProcessMultiLock：将多个锁作为单个实体管理的容器
     */
    @Test
    @SuppressWarnings("all")
    public void zookeeperLockTest() {
        //创建zookeeper的客户端
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();
        //创建分布式锁（InterProcessMutex可重入锁），锁空间的根节点路径为/curator/lock
        InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");

        Thread thread1 = new Thread(() -> {
            try {
                //获得锁，若没有获得锁，一直等待
                //锁被占用时永久阻塞等待
                //这里有个地方需要注意，当与zookeeper通信存在异常时，acquire会直接抛出异常，需要使用者自身做重试策略。
                mutex.acquire();
                System.out.println(Thread.currentThread().getName() + ":acquire lock！");
                //模拟耗时操作
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //重入锁，第一次获得后，再次获得时，由于是一个线程，所以只是计数+1
                mutex.acquire();
                System.out.println(Thread.currentThread().getName() + ":acquire lock！");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    //获得几次可重入锁，这里就要释放几次
                    mutex.release();
                    System.out.println(Thread.currentThread().getName() + ":release lock！");
                    mutex.release();
                    System.out.println(Thread.currentThread().getName() + ":release lock！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                mutex.acquire();
                System.out.println(Thread.currentThread().getName() + ":acquire lock！");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    mutex.release();
                    System.out.println(Thread.currentThread().getName() + ":release lock！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();

        //看最下面注释
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //主线程获取释放锁
        try {
            mutex.acquire();
            System.out.println(Thread.currentThread().getName() + ":acquire lock！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mutex.release();
                System.out.println(Thread.currentThread().getName() + ":release lock！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 按照目前的代码跑的话，如果线程2获得锁，阻塞线程1和main线程，那么当线程2释放锁之后，
         * 如果接下来获得锁的是main线程，那么在main线程继续往下走的时候，线程1才获得锁，但是
         * 线程1中有两个模拟的耗时操作，正在执行，但是main线程已经往下走了，执行了client.close();
         * 已经把客户端关闭了，所以线程1中再获取锁的时候就会异常
         * （某次异常情况如下，每次执行不一定出异常，异常也不一定完全相同）：
         *
         * java.io.IOException: Lost connection while trying to acquire lock: /curator/lock
         * 	at org.apache.curator.framework.recipes.locks.InterProcessMutex.acquire(InterProcessMutex.java:91)
         * 	at cn.tang.base.web.TestDistributedLock.lambda$zookeeperLockTest$0(TestDistributedLock.java:33)
         * 	at java.lang.Thread.run(Thread.java:745)
         * java.lang.IllegalMonitorStateException: You do not own the lock: /curator/lock
         * 	at org.apache.curator.framework.recipes.locks.InterProcessMutex.release(InterProcessMutex.java:140)
         * 	at cn.tang.base.web.TestDistributedLock.lambda$zookeeperLockTest$0(TestDistributedLock.java:54)
         * 	at java.lang.Thread.run(Thread.java:745)
         *
         *  如果把下一行注释掉，每次运行都没问题(但是这样不行，用完就要关闭)，
         *  或者
         *  thread1.join();
         *  thread2.join();
         *  让两个子线程先执行。
         *  或者
         *  使用CountDownLatch在子线程中countDown，主线程中await，这样子线程都执行完，主线程的await才能继续往下走。
         */
        client.close();
    }

}
