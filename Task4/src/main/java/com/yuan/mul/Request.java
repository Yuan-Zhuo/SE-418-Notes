package com.yuan.mul;

import java.io.Serializable;
import java.util.Random;

public class Request implements Serializable {
    private Integer num;
    private Long time;

    public Request() {
        Random r = new Random();
        this.num = r.nextInt(10) + 1;
        this.time = System.currentTimeMillis();
    }


    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getNum() {
        return this.num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

