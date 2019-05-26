package com.yuan.mul;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {

    private AtomicInteger proCnt;
    private AtomicInteger successCnt;
    private AtomicInteger failCnt;
    private LinkedBlockingDeque<Request> reqDeque;
    private long timeout;

    public Producer(LinkedBlockingDeque<Request> reqDeque, AtomicInteger proCnt, long timeout) {
        this.reqDeque = reqDeque;
        this.proCnt = proCnt;
        this.successCnt = new AtomicInteger(0);
        this.failCnt = new AtomicInteger(0);
    }

    @Override
    public void run() {
        System.out.println("> Producer: " + Thread.currentThread().getId() + " start.");
        try {
            while (true) {
                /*
                 * produce resource
                 * */
                Integer curproCnt = produce_resource();
                System.out.println("- Produced " + curproCnt + " this time in " + Thread.currentThread().getId());

                /*
                 *  consume resource
                 * */
                if (this.reqDeque.size() < 10) {
                    System.out.println("> Handle as Queue.");
                    try {
                        Request request = this.reqDeque.getLast();
                        handleRequest(request);
                    } catch (NoSuchElementException e) {
                        System.out.println("> No Request Now.");

                    }
                } else {
                    System.out.println("> Handle as Stack.");
                    try {
                        Request request = this.reqDeque.getFirst();
                        handleRequest(request);
                    } catch (NoSuchElementException e) {
                        System.out.println("> Transfer to queue.");
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("> Producer: " + Thread.currentThread().getId() + " terminal.");
        }
    }

    private int produce_resource() {
        Random resource = new Random();
        int curproCnt = resource.nextInt(10) + 1;
        this.proCnt.addAndGet(curproCnt);
        return curproCnt;
    }

    private void handleRequest(Request request) {
        if (request.getNum() <= this.proCnt.get()) {
            /*
             * Timeout Check: ignore request if timeout.
             * */
            if (request.getTime() + timeout > System.currentTimeMillis()) {
                this.failCnt.incrementAndGet();
                System.out.println("- Request: " + request.getNum() + " Timeout.");
            } else {
                this.proCnt.updateAndGet(x -> (x - request.getNum()));
                this.successCnt.incrementAndGet();
                System.out.println("- Request: " + request.getNum() + " handled.");
            }

            this.reqDeque.remove(request);

            System.out.println("> Produce and Handle once.");
        } else {
            System.out.println("- Request: " + request.getNum() + " cannot be handled this time.");
        }
    }

    public AtomicInteger getCount() {
        return this.proCnt;
    }

    public void setCount(AtomicInteger count) {
        this.proCnt = count;
    }

    public LinkedBlockingDeque<Request> getReqDeque() {
        return reqDeque;
    }

    public void setReqDeque(LinkedBlockingDeque<Request> reqDeque) {
        this.reqDeque = reqDeque;
    }

    public AtomicInteger getSuccessCnt() {
        return this.successCnt;
    }

    public AtomicInteger getFailCnt() {
        return this.failCnt;
    }
}
