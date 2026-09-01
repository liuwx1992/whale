package com.zoo.whale.example.thread;

import java.util.concurrent.Semaphore;

public class AdjustableSemaphore {

    private static AdjustableSemaphore instance;


    private static int maxPermits = 5;

    private static int maxSize = 1000;

    private static final ResizeableSemaphore semaphore = new ResizeableSemaphore(maxPermits);

    private AdjustableSemaphore() {

    }

    public static AdjustableSemaphore getInstance() {
        if (instance == null) {
            synchronized (AdjustableSemaphore.class) {
                if (instance == null) {
                    instance = new AdjustableSemaphore();
                }
            }
        }
        return instance;
    }

    public synchronized void setMaxPermits(int newMax) {
        if (newMax < 1) {
            throw new IllegalArgumentException("Semaphore size must be at least 1,"
                    + " was " + newMax);
        }
        int delta = newMax - this.maxPermits;
        if (delta == 0) {
            return;
        } else if (delta > 0) {
            this.semaphore.release(delta);
        } else {
            delta *= -1;
            this.semaphore.reducePermits(delta);
        }
        this.maxPermits = newMax;
    }

    public int availablePermits() {
        return this.semaphore.availablePermits();
    }

    public void release() {
        this.semaphore.release();
    }

    public void acquire() throws InterruptedException {
        this.semaphore.acquire();
    }

    public boolean tryAcquire() {
        return this.semaphore.tryAcquire();
    }

    public int getMaxPermits() {
        return maxPermits;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    private static final class ResizeableSemaphore extends Semaphore {
        private static final long serialVersionUID = 1L;

        ResizeableSemaphore(int permits) {
            super(permits);
        }

        @Override
        protected void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

}
