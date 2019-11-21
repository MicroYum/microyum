package com.microyum.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestTask {

    public static void main(String[] args) {
        ExecutorService ex = Executors.newFixedThreadPool(1);
        ex.execute(new Task());
    }
}

class Task implements Runnable {

    @Override
    public void run() {

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
