package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleStringSubscriber <T> implements Subscriber <T> {
    private AtomicInteger i = new AtomicInteger(0);
    private HashMap<T, Integer> countMap = new HashMap<>();
    @Override
    public void onEvent(T item) {
        printItem(item);
    }

    public void subscribeToBroker (Broker broker){
        broker.subscribe(this);
    }

    public void printItem (T item){
//        System.out.println("Recieved item " + item);
        if (!countMap.containsKey(item)){
            countMap.put(item, 1);
        }
        else {
            countMap.put(item, countMap.get(item) + 1);
        }
        i.incrementAndGet();
    }

    public HashMap<T, Integer> getCountMap(){
        return countMap;
    }
    public void getI (){
        System.out.println("i is " + i + " size of set is " + countMap.size());
        for (T key : countMap.keySet()){
            System.out.println(key + " ----> " + countMap.get(key));
        }
    }
}
