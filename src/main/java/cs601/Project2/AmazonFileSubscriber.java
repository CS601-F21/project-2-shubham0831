/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Contains the subscriber class
 */
package cs601.Project2;

import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;
import java.util.Iterator;
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

    //the object list which contains only the objects we have to write to the output file
    private ArrayList<AmazonObject> objectsToBePrinted;

    //this object list will contain all the objects, the purpose of this is to verify whether
    //we are getting the items in an ordered fashion or not
    private ArrayList<AmazonObject> allObject;

    //contructor arguments
    //the unix time at which we have to split
    private long fileSplitter;

    //the boolean value to let us know whther this subscriber will be writing the old documents or the new
    private boolean writeOldFile;

    //output file location
    private String outputFileLoc;

    //testing purpose
    private int j = 0;

    public AmazonFileSubscriber(long fileSplitter, boolean writeOldFile, String outputFileLoc){
        //constructor
        //constructing the objectsToBePrinted list
        objectsToBePrinted = new ArrayList<>();
        //constructing the list which will contain all the objects
        allObject = new ArrayList<>();

        //defining the locks
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        //initializing the parameters we will need to write the objects to our file
        this.fileSplitter = fileSplitter;
        this.writeOldFile = writeOldFile;
        this.outputFileLoc = outputFileLoc;
    }

    @Override
    public void onEvent(AmazonObject item) {
        //on event method of the subscriber, our subscriber just stores every data in some arrayList
        //once a broker calls its on event
        //since we are writing to the files, and depending on the broker we can have multiple threads
        //trying to write to the file, we use a write lock
//        System.out.println("Current Thread + " + Thread.currentThread().getName());
        writeLock.lock();
        try {
//            writeToFile(item);
            if (writeOldFile && (item.getUnixReviewTime() <= fileSplitter)){
                objectsToBePrinted.add(item);
            }
            else if (!writeOldFile && (item.getUnixReviewTime() > fileSplitter)){
                objectsToBePrinted.add(item);
            }
            allObject.add(item);
            j++;
        } finally {
            writeLock.unlock();
        }
    }


    public void writeFile (){
        //this is the command we give to our subscriber so that it knows that it has to write the files now
        //the arguments are as following
        /**
         * boolean writeOldFile --> lets the method know whether the files we have to write are the old files or the new files
         * outputFileLoc --> is the location where we want our output file to be
         * fileSplitter --> is the timestamp before and including which all documents are considered old, and after which we consider
         *                  the documents to be new
         */

        //we need a write lock, as we will be modifying the arrayList
        writeLock.lock();
        try {
            AmazonFileReaderWriter.writeFile(objectsToBePrinted, outputFileLoc);
            return;
        } finally {
            writeLock.unlock();
        }
    }

    private void removeUnwantedDocuments (){
        /**
         * Method not being used, but not deleted since method contains information on how to delete an item from
         * an arraylist while iterating over it
         */
        //helper method to remove unwanted elements from the list
        //reference java67.com/2018/12/how-to-remove-objects-or-elements-while-iterating-Arraylist-java.html --> to learn how to remove things
        //from an arraylist while iterating through it

        Iterator<AmazonObject> iterator = objectsToBePrinted.iterator();
        while (iterator.hasNext()){
            AmazonObject object = iterator.next();
            if (writeOldFile){
                if (object.getUnixReviewTime() > fileSplitter){
                    iterator.remove();
                }
            }
            else {
                if (object.getUnixReviewTime() <= fileSplitter){
                    iterator.remove();
                }
            }
        }
    }


    public ArrayList<AmazonObject> getAllObjects (){
        //method to return all the objects the subscriber has gotten
        return new ArrayList<>(allObject);
    }

    public ArrayList<AmazonObject> getObjectsToBePrinted (){
        //method to return all the objects that this class will be writing/printing
        return new ArrayList<>(objectsToBePrinted);
    }

    public void subscribeToBroker (Broker broker){
        //method which the subscriber calls to subscribe to a broker
        broker.subscribe(this);
    }
}
