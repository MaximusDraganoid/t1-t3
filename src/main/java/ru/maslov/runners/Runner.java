package ru.maslov.runners;

import ru.maslov.entities.Customer;
import ru.maslov.entities.Storage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Runner {
    /*
       Требуется реализовать систему склад - покупатели, работающую по следующим правилам:
        1. На складе учитывается остаток товара. Начальное количество = 1000

        2. Количество покупателей указывается в параметрах запуска системы.
        Все покупатели стартуют одновременно и совершают покупки параллельно.

        3. Класс покупатель может за одну покупку (я сломался об это слово, термин "закупка" более подходящий, ИМХО)
         "купить" (уменьшить остаток на складе) случайное количество единиц товара от 1 до 10. Если на складе осталось
         меньше требуемого товара, покупатель забирает остаток.

        4. Когда товар на складе заканчивается, каждый покупатель должен вывести в консоль общее
        количество купленного товара, количество покупок и завершить работу.

        5. Требуется обеспечить равномерное количество покупок, т.е. количество покупок, сделанных
        каждым покупателем не должно отличаться больше чем на 1.

        мб через Phaser переделать????
        */


    public static void main(String[] args) {
        System.out.println("Старт программы...");
        //валадиция входных данных
        int numOfCustomers = validInputData(args);

        //склад
        Storage storage = new Storage();

        //мьютекс
        ReentrantLock lock = new ReentrantLock();

        //барьер
        CyclicBarrier barrier = new CyclicBarrier(numOfCustomers);

        Customer[] customers = new Customer[numOfCustomers];

        for (int i = 0; i < numOfCustomers; i++) {
            customers[i] = new Customer(System.currentTimeMillis(), storage, barrier, lock);
        }

        for (int i = 0; i < numOfCustomers; i++) {
            customers[i].start();
        }

        System.out.println("Начинается обработка...");
        for (int i = 0; i < numOfCustomers; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Проблема при ожидании окончания рабоыт потока", e);
            }
        }

        Arrays.sort(customers, Comparator.comparing(Customer::getNumberOfTripsToTheStore));

        System.out.println("Покупки успешно завершены! Результат работы прогарммы:");

        System.out.print("Общее число купленных товаров: ");
        int sum = 0;
        for (Customer customer : customers) {
            sum += customer.getBayedNumOfProduct();
        }
        System.out.print(sum);
        System.out.println();

    }

    private static int validInputData(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException();
        }
        int value = 0;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Переданное значение не является целым числом");
        }

        if (value < 1) {
            throw new RuntimeException("Переданное значение < 1");
        }

        return value;
    }
}
