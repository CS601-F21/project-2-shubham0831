/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Contains the main method
 */
package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  Our AmazonFileSubscriber only subscribes to AmazonObjects
 */
public class AmazonFileSubscriber implements Subscriber<AmazonObject> {
    //declaring the locks
    private ReentrantReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;

    private ArrayList<AmazonObject> objectList;

    //testing purpose
    private int j = 0;

    public AmazonFileSubscriber(){
        //constructor
        //defining the arrayList
        objectList = new ArrayList<>();

        //defining the locks
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    public void onEvent(AmazonObject item) {
        writeLock.lock();
        /**
         *  Todo :
         *      Will eventually write code which creates and writes to the destination file
         *      directly.
         *      But for the time being, using an arrayList to find out if the data we have received
         *      is in order or not
         */
        try {
            objectList.add(item);
            j++;
//            if (objectList.size() == 1304619){
//                System.out.println("got all the items");
//            }
        } finally {
            writeLock.unlock();
        }
    }

    private void writeFile (){
        
    }

    public int getJ () {
        return j;
    }

    public ArrayList<AmazonObject> getObjectList (){
        return new ArrayList<>(objectList);
    }

    public void subscribeToBroker (Broker broker){
        broker.subscribe(this);
    }
}
