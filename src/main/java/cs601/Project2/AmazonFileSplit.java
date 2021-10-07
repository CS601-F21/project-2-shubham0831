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

public class AmazonFileSplit {
    /**
        Explanation :
            This file contains the main method which is responsible for knowing which files is it that the
            user wants to parse and how do they want to parse it.
            To keep the method clean and short, we use create an AmazonUtilities class, which is where we declare
            our broker and create two publishers.
            Then run the two publishers on separate threads and publish the messages.
    */

    //these are the input and output files, since if the user gives valid parameters, we instantiate these in the validateArgs
    private static String androidInputFile;
    private static String homeInputFile;

    private static String oldOutputFile;
    private static String newOutputFile;

    /**
     * The broker which the user wants to use will be declared as an int, the following is what ints the user can input and the
     * corresponding broker
     *      broker type : -1 --> Synchronous Ordered Dispatch Broker
     *      broker type :  0 --> Asynchronous Unordered Dispatch Broker
     *      broker type "  1 --> Asynchronous Ordered Dispatch Broker
     */
    private static int brokerFlag;

    /**
     * To run the program
     *  java -cp project2.jar cs601.Project2.AmazonFileSplit -broker <broker_int> -androidFile <android_file_name> -homeFile <home_file_name>
     *                                                        -oldOuputFiles <old_output_files_name> -newOuputFiles <new_output_files_name>
     *
     *  java -cp project2.jar cs601.Project2.AmazonFileSplit -broker -1 -androidFile /home/shubham/IdeaProjects/project-2-shubham0831/Apps_for_Android_5.json -homeFile /home/shubham/IdeaProjects/project-2-shubham0831/Home_and_Kitchen_5.json -oldOuputFiles /home/shubham/IdeaProjects/project-2-shubham0831/OldFiles.json -newOuputFiles /home/shubham/IdeaProjects/project-2-shubham0831/newFiles.json
     *  */

    public static void main (String[] args){
        if (!validateArgs(args)){
            //if the args are not valid we do not execute anything
            return;
        }

        //we need to keep a track of how long each broker takes, and hence we use this variable to keep a track of when we started the program
        Instant start = Instant.now();

        /**
         * We create the two publisher threads inside this class
         * Namely at lines (153, 160), (192, 200), (234, 241)
         * It is these two publishers that actually publish to the brokers
         *
         * utilities will let the main thread to come back to this class only when the broker is done
         * sending all the data to the subscribers keeping a track of the time it took the broker to publish all the messages
         */
        //actually running the program
        AmazonUtilities utilities = new AmazonUtilities(androidInputFile, homeInputFile, oldOutputFile, newOutputFile, brokerFlag);

        //time when the publisher has finished publishing all the documents
        Instant finish = Instant.now();

        //this is the total time it took for our brokers to publish all the documents
        long timeForSubscriberToReceiveEverything = Duration.between(start,finish).toMillis();

        //printing out the total time it took the brokers to send all te messages to the subscribers
        System.out.println("Total time taken for subscriber to get all items : " + timeForSubscriberToReceiveEverything+ "\n");

        //method we call to check the correctness of our program, more details about the correctness can be found in the method itself
        utilities.checkExecutionCorrectness();

        //we then start writing the files
        /**
         * Since we have to write two separate files independent of each other, we have used 2 separate  threads to speed up the writing process.
         * This whole implementation is done in the writeBothFiles() method and further details can be found there
         */
        utilities.writeBothFiles();

        //this is the finish time we use to keep a track of the time we have finished writing our files
        finish = Instant.now();

        //this is the total time it has taken the program to do every thing, including writing the files
        long totalDuration = Duration.between(start,finish).toMillis();
        System.out.println("Total time taken for execution of the whole program : " + totalDuration);

    }

    private static boolean validateArgs (String[] args){
        //method to check whether the arguments we have received are valid or not
        //if the total arguments we get is not equal to 5 (2 for input files, 2 for output files, and 1 for type of broker to be used)
        //we return false
        if (args.length != 10){
            return false;
        }
        //our broker type will be specified as an int
        /**
         * broker type -1 --> Synchronous Ordered Dispatch Broker
         * broker type  0 --> Asynchronous Unordered Dispatch Broker
         * broker type  1 --> Asynchronous Ordered Dispatch Broker
         */
        //if the broker type is not greater than equal to -1 and less than equal to 1
        //that is not a valid arg
        int userInputBrokerType = Integer.parseInt(args[1]);
        if (!(userInputBrokerType >= -1) && !(userInputBrokerType <= 1)){
            System.err.println("Invalid Broker Type");
            return false;
        }

        brokerFlag = userInputBrokerType;

        //since args 2,4,6,... will be for file description, ie. -reviewFile ,etc
        for (int i = 2; i < args.length; i = i+2){
            if (args[i].endsWith(".json")){
                System.err.println("Invalid Arguments, first we need the file description");
                return false;
            }
        }

        //args 3,5,7,.... will be the json files
        for (int i = 3; i < args.length; i = i+2){
            if (!args[i].endsWith(".json")){
                System.err.println("Invalid Arguments, only accepting json");
                return false;
            }
        }

        androidInputFile = args[3];
        homeInputFile = args[5];
        oldOutputFile = args[7];
        newOutputFile = args[9];

        return true;
    }
}
