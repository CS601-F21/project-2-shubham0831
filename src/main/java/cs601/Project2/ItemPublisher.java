/**
*   Author Name  : Shubham Pareek
*   Author Email : spareek@dons.usfca.edu
*   Class function : Class containing the blueprint of a generic publisher
*/
package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Publisher.Publisher;


import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * How a publisher works is that first we declare a publisher of a generic type then we add brokers to it. These are the brokers
 * the publisher will be publishing to. Then everytime user wants the publisher to publish, they call the publish method, and that
 * method then triggers the publish method in the broker class, with the item as the parameter the publisher wants to send
 */

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
        //constructing the set of brokers
        brokerList = new HashSet<>();

        //defining the locks
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public void addBroker (Broker broker){
        //method to add the brokers
        //we use a write lock over here, since we cannot allow a publisher to be publishing at the
        //same time we add a broker.
        //hence the write lock over here and the read lock in the publish method
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
        //read lock used, since there can be concurrent write operations in the addBroker() method
        //so we want to ensure that our publisher is thread safe
        readLock.lock();
        try {
            for (Broker broker : brokerList) {
                broker.publish(item);
            }
        } finally {
            readLock.unlock();
        }
    }
}
