package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Publisher.Publisher;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ItemPublisher <T> implements Publisher <T> {
    //is the list of brokers the publisher publishes to
    private HashSet<Broker> brokerList;

    //we need a read and a write lock, incase two threads come and one is adding to the broker list
    //and the other is publishing an item, and hence reading from the broker list
    private ReentrantReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;

    public ItemPublisher (){
        //constructor
        brokerList = new HashSet<>();

        //defining the locks
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public void addBroker (Broker broker){
        writeLock.lock();
        try {
            brokerList.add(broker);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void publish(T item) {
        //publish method
        //this is where we publish the data the publisher wants to publish to all the brokers
        readLock.lock();
        try {
            for (Broker broker : brokerList) {
                broker.publish(item);
            }
        } finally {
            readLock.unlock();
        }
    }


    public void stopPublishing (){
        //the method called by the publisher to shut down the broker and for it to stop publishing
        writeLock.lock();
        try {
            for (Broker broker : brokerList){
                broker.shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
