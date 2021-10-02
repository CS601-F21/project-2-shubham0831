package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;

public class SynchronousOrderedDispatchBroker <T> implements Broker <T> {
    private HashSet<Subscriber> subList = new HashSet<>();
    @Override
    public synchronized void publish(T item) {
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
