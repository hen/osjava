package com.generationjava.math;

// extend Number?
public class Average {

    private int sum;
    private int num;

    public Average() {
    }

    public Average(int start) {
        add(start);
    }

    public void add(int val) {
        sum += val;
        num++;
    }

    public int average() {
        return sum/num;
    }

}
