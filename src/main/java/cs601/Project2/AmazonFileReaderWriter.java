/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Read and write the json file
 */
package cs601.Project2;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;

/**
 *  Explanation:
 *      This class takes as input a file location, opens and parses the file to jsonObject and then stores
 */
public class AmazonFileReaderWriter {
    //the location of the file which we have to open and parse
    private String fileLoc;

    //this is the list which contains all the json objects
    private ArrayList<AmazonObject> objectList;

    //is this charset of the file
    //variable is static because we will be using it in the static write method
    private static final String charSet = "ISO-8859-1";

    //for testing
    private int j = 0;

    public AmazonFileReaderWriter(String fileLoc){
        //constructor

        //initializing the fileLoc
        this.fileLoc = fileLoc;

        //initializing the objectList
        objectList = new ArrayList<>();

        //once we have initialized everything, we can then straightaway
        //start parsing the file
        parseFile();
    }

    //method for parsing the file
    private void parseFile () {
        //defining the file
        File file = new File(fileLoc);

        //starting gson
        Gson gson = new Gson();

        //using buffered reader to read through file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet))){
            String line;
            while ((line = br.readLine()) != null){
                j += 1;
                try {
                    //converting file obj to Amazon Object Class
                    AmazonObject object = gson.fromJson(line, AmazonObject.class);

                    //adding the object to the object list
                    objectList.add(object);

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reference : https://stackoverflow.com/a/2885241 to learn how to write a file
    public static void writeFile (ArrayList<AmazonObject> sourceList, String outputFileLoc){
        /**
         *  static write method
         *  Takes in a source ArrayList and writes its content to the outputFileLoc
         */

        Gson gson = new Gson();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileLoc), charSet))) {
            for (AmazonObject object : sourceList){
                String json = gson.toJson(object);
                writer.write(json);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reference https://mkyong.com/java/how-to-append-content-to-file-in-java/ -> to learn how to append to file
    public static void writeToFile (AmazonObject item, String outputFile){
        /**
         * We aren't actually using this method, but this method has not been deleted since it contains the info on
         * how to append lines to an already existing file
         */
        Gson gson = new Gson();
        try (Writer writer = new BufferedWriter(new FileWriter(outputFile, true))){
            String json = gson.toJson(item);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //getter for the objectList
    public ArrayList<AmazonObject> getItems (){
        //this method returns a new ArrayList so that encapsulation is maintained
        return new ArrayList<>(objectList);
    }

}
