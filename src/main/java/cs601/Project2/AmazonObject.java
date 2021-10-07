/**
 Author Name : Shubham Pareek
 Author Email : spareek@dons.usfca.edu
 Class function : Class contains the blueprint for a document in the given file
 */
package cs601.Project2;

import java.util.ArrayList;

public class AmazonObject {
    //these are all the fields in the json files
    private String reviewerID;
    private String asin;
    private String reviewerName;
    private ArrayList helpful;
    private String reviewText;
    private float overall;
    private String summary;
    private long unixReviewTime;
    private String reviewTime;

    public AmazonObject(String reviewerId, String asin, String reviewerName,
                         ArrayList helpful, String reviewText, float overall,
                         String summary, long unixReviewTime, String reviewTime)
    {
        this.reviewerID = reviewerId;
        this.asin = asin;
        this.reviewerName = reviewerName;
        this.helpful = helpful;
        this.reviewText = reviewText;
        this.overall = overall;
        this.summary = summary;
        this.unixReviewTime = unixReviewTime;
        this.reviewTime = reviewTime;
    }

    //we will be needing the unixTime when we finally divide the reviews
    public long getUnixReviewTime() {
        return unixReviewTime;
    }

}
