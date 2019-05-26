package com.yuan.mul;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class MulApp {

    public static void main(String[] args) throws InterruptedException {
        /*
         * Init settings: timeout: 200
         *                reqsize: 50
         * */
        LinkedBlockingDeque<Request> reqDeque = new LinkedBlockingDeque<>(50);
        AtomicInteger reqCnt = new AtomicInteger();
        AtomicInteger proCnt = new AtomicInteger();
        int timeout = 2000;


        Producer producer1 = new Producer(reqDeque, proCnt, timeout);
        Consumer consumer1 = new Consumer(reqDeque, reqCnt, timeout);
        ExecutorService service = Executors.newCachedThreadPool();


        service.execute(producer1);
        service.execute(consumer1);

        Thread.sleep(10 * 1000);
        System.out.println("> Terminal Now.");
        service.shutdownNow();
        System.out.println("> Terminated.");

        System.out.println("= requests: " + reqCnt + ". produces " + proCnt + ".");
        System.out.println("= success: " + producer1.getSuccessCnt() + ". fails " + producer1.getFailCnt());
    }

}