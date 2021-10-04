package cs601.Project2;


import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StringALSubscriber <T> implements Subscriber <T> {
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private int i = 0;
    private ArrayList<T> objList = new ArrayList<>();

    @Override
    public void onEvent(T item) {
        printItem(item);
    }

    public void subscribeToBroker (Broker broker){
        broker.subscribe(this);
    }

    public void printItem (T item){
//        System.out.println("Recieved item " + item);
        writeLock.lock();
        try{
            objList.add(item);
            i++;
        }finally {
            writeLock.unlock();
        }
    }

    public ArrayList<T> getObjList() {
        readLock.lock();
        try {
            return objList;
        }finally {
            readLock.unlock();
        }
    }

    public void getI (){
        readLock.lock();
        try {
            System.out.println("i is " + i + " size of AL is " + objList.size());
            System.out.println(objList);
        }finally {
            readLock.unlock();
        }
    }

}
