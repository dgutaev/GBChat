package com.example.gbchat;

public class Lesson4 {
    private int threadNumber = 1;
    private final int COUNTER = 5;

    public static void main(String[] args) {
        Lesson4 lesson4 = new Lesson4();
        Thread thread1 = new Thread(lesson4::thread1);
        Thread thread2 = new Thread(lesson4::thread2);
        Thread thread3 = new Thread(lesson4::thread3);
        thread1.start();
        thread2.start();
        thread3.start();
    }

    public void thread1() {
        try {
            synchronized (this) {
                for (int i = 0; i < COUNTER; i++) {
                    while (threadNumber != 1) {
                        wait();
                    }
                    System.out.print("A");
                    threadNumber++;
                    notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void thread2() {
        try {
            synchronized (this) {
                for (int i = 0; i < COUNTER; i++) {
                    while (threadNumber != 2) {
                        wait();
                    }
                    System.out.print("B");
                    threadNumber++;
                    notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void thread3() {
        try {
            synchronized (this) {
                for (int i = 0; i < COUNTER; i++) {
                    while (threadNumber != 3) {
                        wait();
                    }
                    System.out.print("C");
                    threadNumber = 1;
                    notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
