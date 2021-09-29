package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.BasicSubscriber;


public interface Broker <T> {
    /**
     * Called by a publisher to publish a new item. The
     * item will be delivered to all current subscribers.
     *
     * @param item
     */
    public void publish(T item);

    /**
     * Called once by each subscriber. Subscriber will be
     * registered and receive notification of all future
     * published items.
     *
     * @param subscriber
     */
    public void subscribe(BasicSubscriber subscriber);

    /**
     * Indicates this broker should stop accepting new
     * items to be published and shut down all threads.
     * The method will block until all items that have been
     * published have been delivered to all subscribers.
     */
    public void shutdown();
}
