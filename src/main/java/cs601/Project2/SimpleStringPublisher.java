package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Publisher.Publisher;

import java.util.ArrayList;
import java.util.HashSet;

public class SimpleStringPublisher <T> extends Thread implements Publisher<T>{
    //HashSet<SimpleStringBroker> brokerList = new HashSet<>(); //to ensure that we only have string brokers
    HashSet<Broker<T>> secondBrokerList = new HashSet<>(); //another way to do it, but this will make the publisher publish to any broker

    @Override
    public synchronized void publish(T item) {
//        System.out.println(Thread.currentThread().getName() + " is handling " + item);
        for (Broker b : secondBrokerList){
            b.publish(item);
        }
    }

    public void addBroker (Broker b){
        secondBrokerList.add(b);
    }


}
