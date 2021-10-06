/**
 * Author Name  : Shubham Pareek
 * Author Email : spareek@dons.usfca.edu
 * Class function : Contains all the utility methods
*/
package cs601.Project2;

import cs601.PubSub.Brokers.AsyncOrderedDispatchBroker;
import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;
import cs601.PubSub.Brokers.Broker;
import cs601.PubSub.Brokers.SynchronousOrderedDispatchBroker;

import java.util.ArrayList;

/**
 * The purpose of this class is to actually do all the required work we need to do, from parsing the amazon files to creating
 * AmazonObjects based on the json documents in the file, to defining the creating and defining the brokers, publishers and subscribers.
 *
 * This file contains specific code for the purpose of demonstration of the functionality of the brokers and is written accordingly.
 */

public class AmazonUtilities {
    //both the publishers will run on separate threads
    //this is the publisher which will publish the json file Apps_for_Android_5.json
    private ItemPublisher<AmazonObject> androidFilePublisher;
    //this is the publisher which will publish the json file Home_and_Kitchen_5.json
    private ItemPublisher<AmazonObject> homeFilePublisher;

    //defining the subscribers. Both the subscribers will receive all the files, and will
    //separate the files based on old and new, once they have actually received the files
    //this is the subscriber which will filter out and write only the old reviews to the json
    //file
    private AmazonFileSubscriber oldAmazonFileSubscriber;
    //this is the subscriber which will filter out and write only the new reviews to the json
    //file
    private AmazonFileSubscriber newAmazonFileSubscriber;

    //defining the brokers
    //before we define the broker, we need to know which broker to use, and this int is for that purpose
    /**
     * Following are how this int works:
     *          brokerFlag == -1 ----> This means that we will be using the SynchronousOrderedDispatchBroker
     *          brokerFlag == 0  ----> This means that we will be using the AsynchronousUnorderedDispatchBroker
     *          brokerFlag == 1  ----> This means that we will be using the AsynchronousOrderedDispatchBroker
     *
     *          if the brokerFlag is anything else, we will do nothing and the messages will not get published
     *
     *          We will be getting this broker flag as an input in our constructor
     */
    private int brokerFlag;
    //all our brokers will be brokers of object of type AmazonObject, which is what we will cast the
    //individual documents on the json file to
    //firstly we have the SynchronousOrderedDispatchBroker
    private SynchronousOrderedDispatchBroker<AmazonObject> syncOrderedBroker;
    //then the AsyncUnorderedDispatchBroker
    private AsyncUnorderedDispatchBroker<AmazonObject> asyncUnorderedBroker;
    //finally we have the AsynchronousOrderedDispatchBroker
    private AsyncOrderedDispatchBroker<AmazonObject> asyncOrderedBroker;

    //we also need to store our input file locations
    //we store the location of our android file in this variable
    private String androidInputFileLocation;
    //we store the location of our home file in this variable
    private String homeInputFileLocation;

    //we will also be needing the output file locations as well
    //we store the location of the old documents in this variable
    private String oldDocumentsFileLocation;
    //we store the location of the new documents in this variable
    private String newDocumentsFileLocation;

    //we do not need a specified charset, as that has been hardcoded this time

    //these variables are for the AmazonFileParser class, these object takes in as input
    //the file location of a json file, then creates a list of AmazonObject which can
    //then be used to actually start publishing the documents
    private AmazonFileParser androidFileParser;
    private AmazonFileParser homeFileParser;

    //we will also be needing these two arrayLists as these are from where we actually publish things
    private ArrayList<AmazonObject> androidFileObjects;
    private ArrayList<AmazonObject> homeFileObjects;

    public AmazonUtilities(String androidInputFileLocation, String homeInputFileLocation, String oldDocumentsFileLocation, String newDocumentsFileLocation, int brokerFlag){
        //in this constructor, we take the input and output files and define them. After doing that we parse the input
        //files and store them in the FileObjects arraylist
        this.androidInputFileLocation = androidInputFileLocation;
        this.homeInputFileLocation = homeInputFileLocation;
        this.oldDocumentsFileLocation = oldDocumentsFileLocation;
        this.newDocumentsFileLocation = newDocumentsFileLocation;

        //constructing the broker flag
        this.brokerFlag = brokerFlag;

        //we do not need to use the "this" keyword from now on as we have parsed all the user inputs and have no more conflicting variable names
        //we have constructed the parser, as soon as we do this, the parser starts parsing the file and storing it in some ArrayList
        androidFileParser = new AmazonFileParser(androidInputFileLocation);
        homeFileParser = new AmazonFileParser(homeInputFileLocation);

        //we then get the ArrayList from the FileParsers using the getters and set those ArrayList as the androidFileObjects and homeFileObject
        //Arraylists respectively
        //getting the androidFileObjects which have to be published
        androidFileObjects = androidFileParser.getItemsToBePublished();
        //getting the homeFileObjects which have to be published
        homeFileObjects = homeFileParser.getItemsToBePublished();

        //we are initializing our publishers over here
        androidFilePublisher = new ItemPublisher<>();
        homeFilePublisher = new ItemPublisher<>();

        //we are initializing our subscribers over here
        oldAmazonFileSubscriber = new AmazonFileSubscriber();
        newAmazonFileSubscriber = new AmazonFileSubscriber();

        //now at this stage we have our files which have to be published in the proper format, all we have to do this initialize the brokers, have
        //publishers add the brokers so that they publish to said broker and have subscriber subscribe to the brokers
        //what broker we start and initialize depends on the value of the brokerFlag as we have defined above.
        //if the brokerFlag == -1, we start the SynchronousOrderedDispatchBroker
        if (brokerFlag == -1){
            startSyncOrderedBroker();
        }
        //if the brokerFlag == 0, we start the AsyncUnorderedDispatchBroker
        else if (brokerFlag == 0){
            startAsyncUnorderedBroker();
        }
        //if the brokerFlag == 1, we start the AsyncOrderedDispatchBroker
        else if (brokerFlag == 1){
            startAsyncOrderedBroker();
        }

    }

    /**
     * The following three methods are the three implementation of the brokers
     */

    private void startSyncOrderedBroker() {
        //over here is where we will initialize our sync ordered broker
        syncOrderedBroker = new SynchronousOrderedDispatchBroker<>();
        //we add the broker to our publisher, as each publisher can publish to
        //multiple brokers
        androidFilePublisher.addBroker(syncOrderedBroker);
        homeFilePublisher.addBroker(syncOrderedBroker);
        //we make our subscribers subscribe to the broker
        oldAmazonFileSubscriber.subscribeToBroker(syncOrderedBroker);
        newAmazonFileSubscriber.subscribeToBroker(syncOrderedBroker);

        /**
         * Could have instantiated the threads earlier, as in case of all the brokers
         * the purpose of the thread is the same.
         * Chose not to do that as this makes showcasing the implementation quite simpler
         */
        Thread androidFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < androidFileObjects.size(); i++){
                AmazonObject item = androidFileObjects.get(i);
                androidFilePublisher.publish(item);
            }
        });

        Thread homeFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < homeFileObjects.size(); i++){
                AmazonObject item = homeFileObjects.get(i);
                homeFilePublisher.publish(item);
            }
        });

        androidFilePublisherThread.start();
        homeFilePublisherThread.start();

        //we use this while loop as we cannot be sure when our threads are done with publishing, and
        //if we shutdown our thread earlier and continue to send publish request, we will keep getting
        //error messages
        while (androidFilePublisherThread.isAlive() || homeFilePublisherThread.isAlive()){}
        syncOrderedBroker.shutdown();
    }

    private void startAsyncUnorderedBroker() {
        //over here is where we will initialize our async unordered broker
        asyncUnorderedBroker = new AsyncUnorderedDispatchBroker<>();
        //we add the broker to our publisher, as each publisher can publish to
        //multiple brokers
        androidFilePublisher.addBroker(asyncUnorderedBroker);
        homeFilePublisher.addBroker(asyncUnorderedBroker);
        //we make our subscribers subscribe to the broker
        oldAmazonFileSubscriber.subscribeToBroker(asyncUnorderedBroker);
        newAmazonFileSubscriber.subscribeToBroker(asyncUnorderedBroker);

        //declaring the threads for each publisher and assigning it, its task
        Thread androidFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < androidFileObjects.size(); i++){
                AmazonObject item = androidFileObjects.get(i);
                androidFilePublisher.publish(item);
            }
        });

        Thread homeFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < homeFileObjects.size(); i++){
                AmazonObject item = homeFileObjects.get(i);
                homeFilePublisher.publish(item);
            }
        });

        //starting the threads
        androidFilePublisherThread.start();
        homeFilePublisherThread.start();

        //we use this while loop as we cannot be sure when our threads are done with publishing, and
        //if we shutdown our thread earlier and continue to send publish request, we will keep getting
        //error messages
        while (androidFilePublisherThread.isAlive() || homeFilePublisherThread.isAlive()){}
        asyncUnorderedBroker.shutdown();
    }

    private void startAsyncOrderedBroker() {
        //over here is where we will initialize our async ordered broker
        asyncOrderedBroker = new AsyncOrderedDispatchBroker<>();
        //we add the broker to our publisher, as each publisher can publish to
        //multiple brokers
        androidFilePublisher.addBroker(asyncOrderedBroker);
        homeFilePublisher.addBroker(asyncOrderedBroker);
        //we make our subscribers subscribe to the broker
        oldAmazonFileSubscriber.subscribeToBroker(asyncOrderedBroker);
        newAmazonFileSubscriber.subscribeToBroker(asyncOrderedBroker);

        //declaring the threads for each publisher and assigning it, its task
        Thread androidFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < androidFileObjects.size(); i++){
                AmazonObject item = androidFileObjects.get(i);
                androidFilePublisher.publish(item);
            }
        });

        Thread homeFilePublisherThread = new Thread(() -> {
            for (int i = 0; i < homeFileObjects.size(); i++){
                AmazonObject item = homeFileObjects.get(i);
                homeFilePublisher.publish(item);
            }
        });

        //starting the threads
        androidFilePublisherThread.start();
        homeFilePublisherThread.start();

        //we use this while loop as we cannot be sure when our threads are done with publishing, and
        //if we shutdown our thread earlier and continue to send publish request, we will keep getting
        //error messages
        while (androidFilePublisherThread.isAlive() || homeFilePublisherThread.isAlive()){}
        asyncOrderedBroker.shutdown();
    }

    /**
     * End of broker method implementation
     */

    public ArrayList<AmazonObject> getWhatOldSubscriberReceived() {
        return oldAmazonFileSubscriber.getObjectList();
    }

    public ArrayList<AmazonObject> getWhatNewSubscriberReceived() {
        return newAmazonFileSubscriber.getObjectList();
    }

}
