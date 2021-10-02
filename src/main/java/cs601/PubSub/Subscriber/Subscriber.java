package cs601.PubSub.Subscriber;

public interface Subscriber <T> {
    /**
     * Called by the Broker when a new item
     * has been published.
     * @param item
     */
    /**
     * Essentially used by the broker to send the item to
     * the subscriber. The item which is sent by the broker
     * is the "T item" So for example in our use case, we will
     * take this item and send it to some other method (ie. printFile)
     * to print the given item to a file or anything else.
     */
    public void onEvent(T item);

}
