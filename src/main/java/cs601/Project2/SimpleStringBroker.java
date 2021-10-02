package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;

public class SimpleStringBroker <T> implements Broker <T> {
    private HashSet<Subscriber> subList = new HashSet<>();
    @Override
    public void publish(T item) {
        for (Subscriber sub : subList){
            sub.onEvent(item);
        }
    }

    @Override
    public synchronized void subscribe(Subscriber subscriber) {
        subList.add(subscriber);
    }

    @Override
    public void shutdown() {

    }

}
