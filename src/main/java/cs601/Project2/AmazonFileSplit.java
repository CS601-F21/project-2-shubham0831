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
                We might be able to see performance gains, if inside each publisher we use a threadpool, or different threads to actually
                publish the message to the subscriber

            The publishers will not filter the items according to old or new, instead it is the subscriber which does that.
    */
    public static void main (String[] args){
        /**
         * TODO:
         *    Write implementation where we parse the input and output files from the args array
         */
        String file1 = "/home/shubham/IdeaProjects/project-2-shubham0831/Apps_for_Android_5.json";
        String file2 = "/home/shubham/IdeaProjects/project-2-shubham0831/Home_and_Kitchen_5.json";

//        SynchronousOrderedDispatchBroker asyncBroker = new SynchronousOrderedDispatchBroker();
//        AsyncUnorderedDispatchBroker asyncBroker = new AsyncUnorderedDispatchBroker();
        AsyncOrderedDispatchBroker asyncBroker = new AsyncOrderedDispatchBroker();
        AmazonFilePublisher publisher1 = new AmazonFilePublisher(file1);
        AmazonFilePublisher publisher2 = new AmazonFilePublisher(file2);
        publisher1.addBroker(asyncBroker);
        publisher2.addBroker(asyncBroker);
        AmazonFileSubscriber subscriber1 = new AmazonFileSubscriber();
        AmazonFileSubscriber subscriber2 = new AmazonFileSubscriber();
        subscriber1.subscribeToBroker(asyncBroker);
        subscriber2.subscribeToBroker(asyncBroker);

        publisher1.startPublishing();
        publisher2.startPublishing();

        asyncBroker.shutdown();

//        try {
//            TimeUnit.SECONDS.sleep(13);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        ArrayList<AmazonObject> list1 = subscriber1.getObjectList();
        ArrayList<AmazonObject> list2 = subscriber1.getObjectList();

        System.out.println(list1.size() + " ---- " + list2.size());

//        for (int i = 0; i < list1.size(); i++){
//            if (list1.get(i) != list2.get(i)){
//                System.out.println("Two unordered items found");
//                break;
//            }
//        }

        System.out.println("Done");
    }
}
