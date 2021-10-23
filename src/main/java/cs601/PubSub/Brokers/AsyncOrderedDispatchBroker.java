/**
 Author Name : Shubham Pareek
 Author mail : spareek@dons.usfca.edu
 Class function : Async Ordered Broker
 */
package cs601.PubSub.Brokers;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;
import cs601.Concurrent.CS601BlockingQueue;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

        //boolean variable to keep track of whether the broker is alive or not
        brokerIsAlive = true;

        //initializing the dispatch thread
        initializeDispatchThread();

        //separate method to start the dispatch thread
        startDispatchThread();
    }

    private void initializeDispatchThread (){
        //method to initialize the dispatch thread

        //the dispatch thread will stay alive till the broker is alive and will keep
        //polling the blocking queue for items. If the polling method returns null, we
        //do nothing and keep the thread alive
        dispatchThread = new Thread(() -> {
            //fixed bug where we didn't wait for the queue to get empty before shutting down the broker
                while (brokerIsAlive || !queue.isEmpty()){
                    T item = queue.poll(1000);
                    if (item != null){
                        //need read lock as thread will be reading from the subList
                        readLock.lock();
                        try {
                            for (Subscriber s : subscribers) {
                                s.onEvent(item);
                            }
                        } finally {
                            readLock.unlock();
                        }
                    }
                    //if for whatever reason our thread gets interrupted, we stop and terminate it
                    if (Thread.interrupted()){
                        return;
                    }
                }
        });
    }

    private void startDispatchThread (){
        //method to start the dispatch thread
        dispatchThread.start();
    }

    @Override
    public void publish(T item) {
        //not using locks here, since all this method does is put the item in the blocking queue
        //the put method of the blocking queue is a synchronous one, and hence will not allow more
        //than one thread to get access to it and hence is threadsafe
        if (brokerIsAlive){
            queue.put(item);
        }
        else {
            //instead of printing out our error messages normally, we use system.err.println as that is the more apt tool to use in this case
            System.err.println("Broker is shutdown, and currently not accepting publish requests.");
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        //is the method which the subscriber will call to subscribe to the broker
        if (brokerIsAlive){
            //cannot add subscribers while a thread is publishing items
            writeLock.lock();
            try {
                subscribers.add(subscriber);
            } finally {
                writeLock.unlock();
            }
        }
        else {
            System.err.println("Broker is shut down and not accepting requests");
        }
    }

    @Override
    public synchronized void shutdown() {
        //Once the user calls the shutdown method, this will not allow any threads to publish the remaining
        //items. Since shutdown has to block the process, we use the main thread to complete all the other publish
        //calls then it returns to the call.
        //This ensures that the items that have already been published before will get published

        System.out.println("shutting down async ordered broker\n");

        //changing broker status and deactivating it
        brokerIsAlive = false;

        //once the user calls shutdown, we invoke the join method and wait for the dispatchThread to do its work
        //then only we return to the caller
        try {
            dispatchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

