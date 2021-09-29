package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.BasicSubscriber;

public class SynchronousOrderedDispatchBroker <T> implements Broker <T> {
    @Override
    public void publish(T item) {

    }

    @Override
    public void subscribe(BasicSubscriber subscriber) {

    }

    @Override
    public void shutdown() {

    }
}
