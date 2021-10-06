/**
 Author Name : Shubham Pareek
 Author mail : spareek@dons.usfca.edu
 Class function : Async Ordered Broker
 */
package cs601.Testing;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;
import cs601.Concurrent.CS601BlockingQueue;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 Explanation :
     We have a list of subscribers that are subscribed to a broker. Everytime a publisher
     publishes a new message, we immediately insert that message into the blocking queue.
     We have a single thread defined, which then gets an item from the blocking queue and
     then dispatches it to each subscriber.
     The use of a single thread is essential as if we used multiple threads, or a threadpool
     we cannot be certain of the order in which the items will arrive to the subscriber and
     whether they are ordered or not.

     Currently, the order in which the broker receives the item is the same order in which all
     the subscribers gets the item.
 */


public class AsyncOrderedDispatchBroker <T> implements Broker<T> {
    //this is the list of subscribers that subscribe to the broker
    private HashSet<Subscriber> subscribers;
    //this is the blocking queue
    private CS601BlockingQueue <T> queue;
    //declaring the locks
    private ReentrantReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;
    //this is the thread which will actually dispatch the messages
    private Thread dispatchThread;

    //status of the broker, whether of not it is accepting more requests
    private boolean brokerIsAlive;

    public AsyncOrderedDispatchBroker (){
        //constructor
        //initializing the subscribers HashSet
        this.subscribers = new HashSet<>();
        //creating a blocking queue of size 10000
        queue = new CS601BlockingQueue(10000);
        //initializing the locks
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        brokerIsAlive = true;

        //initializing the dispatch thread
        initializeDispatchThread();
        //separate method to start the dispatch thread
        startDispatchThread();
    }

    private void initializeDispatchThread (){
        dispatchThread = new Thread(() -> {
            while (brokerIsAlive){
                T item = queue.poll();
                if (item != null){
                    for (Subscriber s : subscribers){
                        s.onEvent(item);
                    }
                }
                //if for whatever reason our thread gets interrupted, we stop and terminate
                //it
                if (Thread.interrupted()){
                    return;
                }
            }
        });
    }

    private void startDispatchThread (){
        dispatchThread.start();
    }

    @Override
    public void publish(T item) {
        queue.put(item);
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void shutdown() {
        brokerIsAlive = false;
    }
}

