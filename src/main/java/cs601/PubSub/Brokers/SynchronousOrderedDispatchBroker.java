/*
    Author Name : Shubham Pareek
    Author Email : spareek@dons.usfca.edu
    Class function : Synchronous Ordered Broker
*/
package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
    Explanation:
        We have a list of subscribers that are subscribed to a broker. We only allow one thread
        at a time to publish the message that it has to, so the thread which carries the message
        to be published is the same one which actually publishes the message as well
*/

public class SynchronousOrderedDispatchBroker <T> extends Thread implements Broker <T> {
    //set of subscribers
    private HashSet<Subscriber> subList;

    //this is the boolean we use to keep track of whether the broker is currently active or not
    private volatile boolean isActive;

    public SynchronousOrderedDispatchBroker(){
        //constructor
        this.subList = new HashSet<>();

        //initially our broker is active and accepting publish requests
        isActive = true;
    }
    @Override
    public synchronized void publish(T item) {
        //this is the method which the publisher will call

        //the broker will only accept publish requests is it is currently active
        if (!isActive){
            System.out.println("broker is currently shutdown and not accepting any more publish requests");
            return;
        }

        //if the broker is active, the thread which has carried the publishers message is the one that will send it
        //to all the subscribers as well, and only then will it return.
        for (Subscriber sub : subList){
            sub.onEvent(item);
        }
    }

    /*
        Our subscribe method is synchronous with the publish method, this ensures that if a thread is in middle of adding a
        new subscriber, we cannot have another thread try and publish messages. This ensures that our subscriber set is thread
        safe
    */
    @Override
    public synchronized void subscribe(Subscriber subscriber) {
        //subscribe method
        //this is the method the subscriber calls to subscribe to the broker.
        //the subscriber which calls the method, gets added to the subscriber set in the class
        subList.add(subscriber);
    }

    @Override
    public void shutdown() {
        //is the shutdown method called by the user to tell the broker to stop accepting any new publish requests

        //when shutdown, we make the isActive variable false that implies that the broker is not currently active
        isActive = false;
    }
}
