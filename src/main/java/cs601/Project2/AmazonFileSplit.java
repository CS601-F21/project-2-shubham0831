/**
    Author Name : Shubham Pareek
    Author Email : spareek@dons.usfca.edu
    Class function : Contains the main method
*/

/**
 * Notes :
 *      Total objects in Apps_for_Android == 752937
 *      Total objects in Home_and_Kitchen == 551682
 *
 *      AsyncUnordered time = 6.56 seconds
 *      Syncordered time = 7 seconds
 *
 */
package cs601.Project2;

import cs601.PubSub.Brokers.AsyncOrderedDispatchBroker;
import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;
import cs601.PubSub.Brokers.SynchronousOrderedDispatchBroker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AmazonFileSplit {
    /*
        Explanation :
            This file contains the main method which is responsible for knowing which files is it that the
            user wants to parse and how do they want to parse it.
            We will have n publishers (in this case n = 2), which extend the publisher interface, it is the job of these publishers
            to read and parse the file that the user has specified, then to publish the parsed file to the broker as well.

            TODO:
                We might be able to see performance gains, if we use a thread pool to publish the items.

            The publishers will not filter the items according to old or new, instead it is the subscriber which does that.
    */
    public static void main (String[] args){
        /**
         * TODO:
         *    Write implementation where we parse the input and output files from the args array
         *    input parameters will be in the following order javac class {brokerType} {inputFile1} {inputFile2} {outputFile1} {outputFile2}
         *
         * TODO:
         *    For asyncOrderedBroker, implement the poll method as without that we cannot guarantee our performance, as threads may be kept waiting
         *    to insert elements in the blocking queue
         */

        String file1 = "/home/shubham/IdeaProjects/project-2-shubham0831/Apps_for_Android_5.json";
        String file2 = "/home/shubham/IdeaProjects/project-2-shubham0831/Home_and_Kitchen_5.json";

//        SynchronousOrderedDispatchBroker asyncBroker = new SynchronousOrderedDispatchBroker();
//        AsyncUnorderedDispatchBroker asyncBroker = new AsyncUnorderedDispatchBroker();
        AsyncOrderedDispatchBroker asyncBroker = new AsyncOrderedDispatchBroker();

        AmazonFileParser androidParser = new AmazonFileParser(file1);
        AmazonFileParser homeParser = new AmazonFileParser(file2);

        ArrayList<AmazonObject> androidData = androidParser.getItemsToBePublished();
        ArrayList<AmazonObject> homeData = homeParser.getItemsToBePublished();

        ItemPublisher <AmazonObject> androidPublisher = new ItemPublisher<>();
        ItemPublisher <AmazonObject> homePublisher = new ItemPublisher<>();
        androidPublisher.addBroker(asyncBroker);
        homePublisher.addBroker(asyncBroker);

        AmazonFileSubscriber subscriber1 = new AmazonFileSubscriber();
        AmazonFileSubscriber subscriber2 = new AmazonFileSubscriber();
        subscriber1.subscribeToBroker(asyncBroker);
        subscriber2.subscribeToBroker(asyncBroker);

        Thread androidThread = new Thread(() -> {
            for (AmazonObject a : androidData){
                androidPublisher.publish(a);
            }
        });

        Thread homeThread = new Thread(() -> {
            for (AmazonObject a : homeData){
                homePublisher.publish(a);
            }
        });

        androidThread.start(); homeThread.start();

        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        asyncBroker.shutdown();

        ArrayList<AmazonObject> sub1Objects = subscriber1.getObjectList();
        ArrayList<AmazonObject> sub2Objects = subscriber2.getObjectList();

        System.out.println(sub1Objects.size() + " ---- " + sub2Objects.size());

        for (int i = 0; i < sub1Objects.size(); i++){
            if (sub1Objects.get(i).getAsin() != sub2Objects.get(i).getAsin()){
                System.out.println("Found mismatch");
                break;
            }
        }

        System.out.println("Done");
    }
}
