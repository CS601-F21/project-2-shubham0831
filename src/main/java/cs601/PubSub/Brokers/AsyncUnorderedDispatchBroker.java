/**
    Author Name : Shubham Pareek
    Author Email : spareek@dons.usfca.edu
    Class function : Async Unordered Broker
*/
package cs601.PubSub.Brokers;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
    Explanation:
        We have a list of subscribers that are subscribed to a broker. Everytime a publisher
        publishes a new message, we immediately put the message in the thread pool work queue
        where then different threads in the thread pool deliver the messages
*/


public class AsyncUnorderedDispatchBroker <T> implements Broker <T>{
    //this is the list of subscriber that subscribe to this broker
    private HashSet<Subscriber> subList;
    //declaring the threadpool
    private ExecutorService tPool;
    boolean isActive;

    //we will be needing a lock in this class, as writing to the subscriber set and reading from it
    //should not be done at the same time
    private ReentrantReadWriteLock reentrantReadWriteLock;

    //defining the read and write locks respectively
    private Lock readLock;
    private Lock writeLock;

    public AsyncUnorderedDispatchBroker() {
        //constructor
        //our list of subscribers will be stored in a hashset, as this will ensure that we
        //do not have any duplicates
        this.subList = new HashSet<>();
        //our thread pool will consist of 100 threads.
        /*
            TODO:
                Figure out a way or formula so that the number of threads in the Thread pool depend on the publisher and
                how much data does the publisher actually have to publish
        */
        this.tPool = Executors.newFixedThreadPool(100);

        //is a boolean we use to check whether the broker is active/accepting requests or not
        isActive = true;

        //defining the locks
        this.reentrantReadWriteLock= new ReentrantReadWriteLock();
        readLock = reentrantReadWriteLock.readLock();
        writeLock = reentrantReadWriteLock.writeLock();
    }

    @Override
    public void publish(T item) {
        //publish method, this is the method which will be called by the publish to send message to the subscriber

        //the read lock is there, as we need to read from the subList to get a list of subscribers, and we should not be able to do that if
        //another thread is adding subscriber to the same set
        readLock.lock();
        try {
            //the broker will only publish items if it is currently active.
            if (isActive == false) {
                //not throwing an error as that will stop the current threads as well
                //instead of printing out our error messages normally, we use system.err.println as that is the more apt tool to use in this case
                System.err.println("Broker is shutdown, and currently not accepting publish requests.");
                return;
//            throw new RuntimeException("Broker is shutdown, and currently not accepting publish requests. Call the startBroker method to reuse this broker");
            }

            //as soon a thread comes with some item to publish, we put that item in the threadpools task queue
            //we did not have to use a lock or synchronize here, as the task queue in the executor service
            //is a threadsafe data structure.
            //references : https://stackoverflow.com/a/1704054 //to find out whether ExecutorService.threadPool.submit is threadsafe or not
            //every thread will take care of submitting a data to all the subscribers.
            tPool.submit(() -> {
                for (Subscriber sub : subList) {
                    sub.onEvent(item);
                }
            });

            //in the previous implementation, we were creating a new thread everytime we received a new item to publish. That could have lead to
            //a bottleneck eg. if we had billions of data to publish, we would end up creating a billion threads and the overhead for that is not
            //worth it and  might in fact slow the system down.
        } finally {
            //unlock the read lock
            readLock.unlock();

            //we shutdown the threadpool in the shutdown method and not here, since what this will do is that every thread, after doing it's work will
            //try and shutdown the threadpool
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        //subscribe method
        //this is the method that the subscriber calls when it wants to subscribe to a broker
        //we simply add the subscriber to a hashset of subscribers.

        //we use a write lock here to ensure that the subList is thread safe, and no two threads can simultaneously read/write or
        //write/write in the subList set
        writeLock.lock();
        try {
            subList.add(subscriber);
        } finally {
            //unlocking the writeLock
            writeLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        //shutdown method
        //this method will shut down the broker, and will not allow any further publish requests from this broker
        //this method also shut down the thread pool.

        //changing the isActive boolean to false, as this will stop accepting new tasks immediately.
        isActive = false;
        tPool.shutdown();
        try {
            tPool.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
