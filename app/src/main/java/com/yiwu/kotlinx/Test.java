package com.yiwu.kotlinx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ Author:yi wu
 * @ Date: Created in 13:58 2020/8/25
 * @ Description:
 */
public class Test {

    private static void testReentrantLock() {
        ReentrantLock lock = new ReentrantLock();

    }

    private static void textCountDownLatch() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch order = new CountDownLatch(1);
        CountDownLatch answer = new CountDownLatch(5);
        CountDownLatch ready = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread() + "准备");
                        ready.countDown();
                        order.await();
                        System.out.println(Thread.currentThread() + "开始跑");
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println(Thread.currentThread() + "到达终点");
                        answer.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            executor.execute(runnable);

        }

        while (true) {
            if (ready.getCount() == 0) {
                System.out.println("发射命令");
                order.countDown();
                break;
            }
        }

//        try {
//            Thread.sleep((long) (Math.random() * 10000));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("即将发射");
//        order.countDown();
//        System.out.println("发射命令");
        try {
            answer.await();
            System.out.println("全部到达终点");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testThread() {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7};
        char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'};
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        CountDownLatch downLatch = new CountDownLatch(2);

        Thread thread1 = new Thread(() -> {


            for (int i = 0; i < nums.length; i++) {
                synchronized (lock) {
                    System.out.println("数字：" + nums[i]);
                    try {
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                /*try {
                    lock.lock();
                    System.out.println("数字：" + nums[i]);
                    condition.signal();
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("数字释放锁");
                    lock.unlock();
                }*/
            }

        });
        Thread thread2 = new Thread(() -> {
            for (int j = 0; j < chars.length; j++) {
                synchronized (lock) {
                    System.out.println("字符:" + chars[j]);
                    try {
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                try {
//                    lock.lock();
//                    System.out.println("字符:" + chars[j]);
//                    condition.signal();
//                    condition.await();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    System.out.println(".字符 释放锁");
//                    lock.unlock();
//                }
            }
        });
        thread1.start();
        thread2.start();
        try {
            System.out.println("主线程等待");
            downLatch.await();
            System.out.println("主线程结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId() + "Thread3 start");
                try {
                    Thread.sleep(4000);
                    System.out.println("thread3 finish");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId() + "Thread4 start");
                try {
                    Thread.sleep(1000);
                    System.out.println("thread4 sleep finish");
                    thread3.join();
                    System.out.println("thread4 finish====");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//        thread3.start();
//        thread4.start();
    }

    public static int sortList(int[] source) {
        int temp;
        System.out.println(".sortList" + source.length);
        for (int i = 0; i < source.length - 1; i++) {
            temp = source[i];
            for (int j = i + 1; j < source.length; j++) {
                if (temp > source[j]) {
                    temp = source[j];
                    source[j] = source[i];
                    source[i] = temp;
                }
            }
        }

        for (int n = 0; n < source.length - 1; n++) {
            if (source[n] == source[n + 1]) {
                return source[n];
            }

        }
        return -1;

       /* int[] source = {2, 3, 1, 0, 2, 5, 3};
        int result = sortList(source);
        System.out.println(".main"+result);
        for (int i : source) {
            System.out.println(".main:" + i);
        }*/
    }

    public static int findRepetition(int[] input) {
        int temp;
        for (int i = 0; i < input.length; i++) {
            temp = input[i];
            for (int j = i + 1; j < input.length; j++) {
                if (temp == input[j]) {
                    return temp;
                }
            }
        }

        return -1;
    }

    public static void sortStringList(ArrayList<String> source){
        

    }

    public static void main(String[] args) {
        int[] source = {2, 3, 5, 4, 3, 2, 6, 7};
        int result = findRepetition(source);
        System.out.println(".main:" + result);

    }
}
