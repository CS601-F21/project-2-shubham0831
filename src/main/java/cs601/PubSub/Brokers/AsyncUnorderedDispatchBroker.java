package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;


public class AsyncUnorderedDispatchBroker <T> implements Broker <T>{
    private HashSet<Subscriber> subList = new HashSet<>();
    @Override
    public void publish(T item) {
        Thread t1 = new Thread(
                () -> {
                    for (Subscriber sub : subList){
                        sub.onEvent(item);
                    }
                }
        );
        t1.start();
    }

    @Override
    public synchronized void subscribe(Subscriber subscriber) {
        subList.add(subscriber);
    }

    @Override
    public void shutdown() {

    }
}
