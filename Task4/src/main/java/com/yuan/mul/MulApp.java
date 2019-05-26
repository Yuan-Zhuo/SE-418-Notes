package com.yuan.mul;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class MulApp {

    public static void main(String[] args) throws InterruptedException {
        int proNum = 1, conNum = 1;
        RunProcedureConsumer(proNum,conNum);
    }

    private static void RunProcedureConsumer(int proNum,int conNum) throws InterruptedException {
        /*
         * Init settings: timeout: 200
         *                reqsize: 50
         * */
        LinkedBlockingDeque<Request> reqDeque = new LinkedBlockingDeque<>(50);

        AtomicInteger reqCnt = new AtomicInteger(0);
        AtomicInteger reqCanceledCnt = new AtomicInteger(0);
        AtomicInteger proCnt = new AtomicInteger(0);
        AtomicInteger proTotCnt = new AtomicInteger(0);
        AtomicInteger proHandleCnt = new AtomicInteger(0);
        int timeout = 2000;

        int reqValid, reqHandle;

        ArrayList<Producer> theProceduerList = new ArrayList<Producer>();
        ArrayList<Consumer> theConsumerList = new ArrayList<Consumer>();

        for (int i = 0; i < proNum; ++i) {
            theProceduerList.add(new Producer(reqDeque, proCnt, proTotCnt,proHandleCnt, reqCanceledCnt, timeout));
        }

        for (int i = 0; i < conNum; ++i) {
            theConsumerList.add(new Consumer(reqDeque, reqCnt, timeout));
        }

        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < proNum; ++i) {
            service.execute(theProceduerList.get(i));
        }

        for (int i = 0; i < conNum; ++i) {
            service.execute(theConsumerList.get(i));
        }

        Thread.sleep(10 * 1000);
        System.out.println("> Terminal Now.");
        service.shutdownNow();
        Thread.sleep(1000);
        System.out.println("> Terminated.");
        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");

        reqValid = reqCnt.get() - reqCanceledCnt.get() - reqRemain(reqDeque);

        System.out.println("Procedure Handle: " + proHandleCnt.get());

        System.out.println();

        System.out.println("Request Total Create: " + reqCnt);
        System.out.println("Request Remain: " + reqRemain(reqDeque));
        System.out.println("Request Canceled: " + reqCanceledCnt);
        System.out.println("Request Valid: " + reqValid);
    }

    private static int reqRemain(LinkedBlockingDeque<Request> reqDeque){
        int reqCnt = 0;
        for(Request req : reqDeque){
            reqCnt += req.getNum();
        }
        return reqCnt;
    }

}