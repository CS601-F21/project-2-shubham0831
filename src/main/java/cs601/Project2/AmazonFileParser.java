/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Publisher implementation for the Amazon File
 */
package cs601.Project2;

import cs601.PubSub.Brokers.Broker;

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

public class AmazonFileParser {
    //this is the location of the file to be parsed
    private String fileLoc;

    //this is the list which contains all the items that we have to publish
    private ArrayList<AmazonObject> itemsToBePublished;


    public AmazonFileParser(String fileLoc){
        //constructor
        //only takes the file location as the parameter
        this.fileLoc = fileLoc;

        //initializing the itemsToBePublished list
        itemsToBePublished = new ArrayList<>();

        //we call this method, which then parses the file and populates the itemsList
        parseFileAndItemsList(fileLoc);

    }

    public void parseFileAndItemsList (String fileLoc){
        //method to parse the file and to populate the list containing the items which we
        //have to publish
        AmazonFileReaderWriter fileReader = new AmazonFileReaderWriter(fileLoc);
        itemsToBePublished = fileReader.getItems();
    }

    public ArrayList<AmazonObject> getItemsToBePublished () {
        return new ArrayList<>(itemsToBePublished);
    }

}
