package ru.maslov.entities;

import java.util.concurrent.atomic.AtomicBoolean;

public class Storage {
    volatile private Integer numOfProducts = 1000;

    public boolean isEmpty() {
        return numOfProducts == 0;
    }

    public synchronized int giveOutTheProduct(int numOfProducts) {
        int products = 0;
        if (this.numOfProducts < numOfProducts) {
            products = this.numOfProducts;
        } else {
            products = numOfProducts;
        }
        this.numOfProducts -= products;
        return products;
    }

}
