package cs601.Testing;

import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;
import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Publisher.Publisher;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestPub<T> implements Publisher<T>{
    HashSet<Broker<T>> brokerSet = new HashSet<>();

    public void addBroker (Broker<T> b){
        brokerSet.add(b);
    }

    @Override
    public void publish(T item) {
        for (Broker b : brokerSet){
            b.publish(item);
        }
    }
}
