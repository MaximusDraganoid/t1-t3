package ru.maslov.entities;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Customer extends Thread{

    private final static int MAX_GETTING_PRODUCT = 10;

    private Random generator;
    private Storage storage;
    private Integer numberOfTripsToTheStore;
    private Integer bayedNumOfProduct;
    private CyclicBarrier barrier;
    private ReentrantLock lock;

    public Customer(int seed,
                    Storage storage,
                    CyclicBarrier barrier,
                    ReentrantLock lock) {
        generator = new Random(seed);
        this.storage = storage;
        this.barrier = barrier;
        this.lock = lock;
        numberOfTripsToTheStore = 0;
        bayedNumOfProduct = 0;
    }

    @Override
    public void run() {
        while (!storage.isEmpty()) {
            int numOfProducts = generator.nextInt(MAX_GETTING_PRODUCT) + 1;
            lock.lock();
            if (!storage.isEmpty()) {
                numOfProducts = storage.giveOutTheProduct(numOfProducts);
                bayedNumOfProduct += numOfProducts;
                numberOfTripsToTheStore++;
            }
            lock.unlock();

            waiting();
        }

        while (barrier.getNumberWaiting() != 0)
            waiting();

        System.out.println(this);
    }

    private void waiting() {
        try {
            barrier.await();

        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException("Непредвиденное событие: " + e.getMessage(), e);
        }
    }

    public Integer getBayedNumOfProduct() {
        return bayedNumOfProduct;
    }

    public Integer getNumberOfTripsToTheStore() {
        return numberOfTripsToTheStore;
    }

    @Override
    public String toString() {
        return "Поток номер: " + this.getId() +". " +  "Число походов на склад: " + numberOfTripsToTheStore +
                ". Число купленных товаров: " + bayedNumOfProduct;
    }

}
