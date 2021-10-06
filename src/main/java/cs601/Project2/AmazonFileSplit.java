/**
    Author Name : Shubham Pareek
    Author Email : spareek@dons.usfca.edu
    Class function : Contains the main method
*/

/**
 * Notes :
 *      Total objects in Apps_for_Android == 752937
 *      Total objects in Home_and_Kitchen == 551682
 *      Total object overall              == 1304619
 *
 *      AsyncUnordered time = 6.56 seconds
 *      Syncordered time = 7 seconds
 *
 */
package cs601.Project2;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class AmazonFileSplit {
    /*
        Explanation :
            This file contains the main method which is responsible for knowing which files is it that the
            user wants to parse and how do they want to parse it.
            We will have n publishers (in this case n = 2), which extend the publisher interface, it is the job of these publishers
            to read and parse the file that the user has specified, then to publish the parsed file to the broker as well.
    */
    public static void main (String[] args){
        /**
         * TODO:
         *    Write implementation where we parse the input and output files from the args array
         *    input parameters will be in the following order javac class {brokerType} {inputFile1} {inputFile2} {outputFile1} {outputFile2}
         */

        String androidFileInput = "/home/shubham/IdeaProjects/project-2-shubham0831/Apps_for_Android_5.json";
        String homeFileInput = "/home/shubham/IdeaProjects/project-2-shubham0831/Home_and_Kitchen_5.json";

        /**
         * Notes :
         *      All times are in ms
         *      SyncOrderedTime = 5922, 8419, 6035, 5870, 5754, 8077, 7946, 7790
         *      AsyncUnorderedTime = 6046, 8153, 6318, 6071, 6255, 6269, 6248, 6240
         *      AsyncOrderedTime = 5978, 6066, 7716, 7853, 8404
         */
        Instant start = Instant.now();
        AmazonUtilities utilities = new AmazonUtilities(androidFileInput, homeFileInput, "a", "b", 1);
        Instant finish = Instant.now();
        long duration = Duration.between(start,finish).toMillis();
        System.out.println("Total time taken for program is " + duration);
        ArrayList<AmazonObject> sub1 = utilities.getWhatNewSubscriberReceived();
        ArrayList<AmazonObject> sub2 = utilities.getWhatOldSubscriberReceived();

        //both our subscriber should get the same number of objects
        assert sub1.size() == sub2.size();

        System.out.println("Subscriber received " +sub1.size());

        //checking order
        for (int i = 0; i < sub1.size(); i++){
            if (sub1.get(i).getAsin() != sub2.get(i).getAsin()){
                System.out.println("Found mismatch at i " + i);
                break;
            }
        }
    }
}
