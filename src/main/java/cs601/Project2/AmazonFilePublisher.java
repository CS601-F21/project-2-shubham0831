/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Publisher implementation for the Amazon File
 */
package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
    Explanation :
        This is the AmazonFilePublisher class, it takes in as input some Amazon File from the 5-core data set, process the file
        from json to AmazonObject, then it finally publishes all the processed objects to the broker
*/

public class AmazonFilePublisher {
    //this is the location of the file to be parsed
    private String fileLoc;

    //this is the list which contains all the items that we have to publish
    private ArrayList<AmazonObject> itemsToBePublished;

    //is the publisher for this class, and this is what we actually use to publish
    //items from this class
    private ItemPublisher <AmazonObject> publisher;

    //we need the threadPool to ensure that we are able to publish faster
    private ExecutorService threadPool;

    //we will be needing locks for this class to make it threadsafe
    private ReentrantReadWriteLock readWriteLock;
    private Lock readLock;
    private Lock writeLock;

    public AmazonFilePublisher (String fileLoc){
        //constructor
        //only takes the file location as the parameter
        this.fileLoc = fileLoc;

        //initializing the itemsToBePublished list
        itemsToBePublished = new ArrayList<>();

        //initializing the publisher
        publisher = new ItemPublisher<>();

        //initializing the locks
        readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();

        //we call this method, which then parses the file and populates the itemsList
        parseFileAndItemsList(fileLoc);
    }

    private void initializeThreadpool (){
        //constructs the threadPool
        //our threadPool is of size 30, as after a-lot of experimentation, we are getting
        //optimal results with this
        threadPool = Executors.newFixedThreadPool(30);
    }

    public void parseFileAndItemsList (String fileLoc){
        //method to parse the file and to populate the list containing the items which we
        //have to publish
        AmazonFileReader fileReader = new AmazonFileReader(fileLoc);
        itemsToBePublished = fileReader.getItems();
    }

    public void startPublishing (){
        readLock.lock();
        try {
            //this method is important as once the threadPool has been terminated and we want
            //to use it again, we have to initialize it again
            initializeThreadpool();
            threadPool.submit(() -> {
                for (AmazonObject object : itemsToBePublished) {
                    publisher.publish(object);
                }
            });
        } finally {
            //firstly we unlock the readLock
            readLock.unlock();

            //we also have to shut down the threadPool
            //once all the items that are to be published are in the to do of the threadPool, we shut it down
            threadPool.shutdown();
            try {
                threadPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPublishing (){
        publisher.stopPublishing();
    }

    public void addBroker (Broker broker){
        publisher.addBroker(broker);
    }
}
