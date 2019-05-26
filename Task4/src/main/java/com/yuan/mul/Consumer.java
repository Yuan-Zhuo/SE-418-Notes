package com.yuan.mul;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private LinkedBlockingDeque<Request> reqDeque;
    private AtomicInteger reqCnt;
    private long timeout;

    public Consumer(LinkedBlockingDeque<Request> reqDeque, AtomicInteger reqCnt, long timeout) {
        this.reqDeque = reqDeque;
        this.reqCnt = reqCnt;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println("> Consumer: " + Thread.currentThread().getId() + " start.");
        try {
            while (true) {
                /*
                 *  Add Request Here.
                 *  Timeout check: cancel request if timeout.
                 * */
                Request req = new Request();
                if (this.reqDeque.offerLast(req, timeout, TimeUnit.MILLISECONDS)) {
                    System.out.println("+ Request: " + req.getNum() + " Adding: Success.");
                    this.reqCnt.updateAndGet(x -> (x + req.getNum()));
                } else {
                    System.out.println("+ Request Adding: " + req.getNum() + " Timeout.");
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("> Consumer: " + Thread.currentThread().getId() + " terminal.");
        }
    }

}