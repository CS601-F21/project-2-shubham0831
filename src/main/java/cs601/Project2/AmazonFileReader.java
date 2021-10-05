/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Read the json file
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
public class AmazonFileReader {
    //the location of the file which we have to open and parse
    private String fileLoc;
    //this is the list which contains all the json objects
    private ArrayList<AmazonObject> objectList;
    //is this charset of the file
    private final String charSet = "ISO-8859-1";

    //for testing
    private int j = 0;

    public AmazonFileReader(String fileLoc){
        //constructor

        //initializing the fileLoc
        this.fileLoc = fileLoc;

        //initializing the objectList
        objectList = new ArrayList<>();

        //once we have initialized everything, we can then straightaway
        //start parsing the file
        parseFile();
    }

    private void parseFile () {
        File file = new File(fileLoc);
        Gson gson = new Gson();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),charSet))){
            String line;
            while ((line = br.readLine()) != null){
                j += 1;
                try {
                    AmazonObject object = gson.fromJson(line, AmazonObject.class);
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

    //getter for the objectList
    public ArrayList<AmazonObject> getItems (){
        //this method returns a new ArrayList so that encapsulation is maintained
        return new ArrayList<>(objectList);
    }

    public int getJ (){
        return j;
    }


}
