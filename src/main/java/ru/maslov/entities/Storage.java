package ru.maslov.entities;

public class Storage {
    volatile private Integer numOfProducts = 1000;
    //нужен блокировщик, который будет ограничивать доступ других потоков, пока третий забирает

    public boolean isEmpty() {
        return numOfProducts == 0;
    }

    public int getNumOfProducts() {
        return numOfProducts;
    }

    public int giveOutTheProduct(int numOfProducts) {
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
