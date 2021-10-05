/**
    Author Name : Shubham Pareek
    Author Email : spareek@dons.usfca.edu
    Class function : Interface for a Publisher
*/

package cs601.PubSub.Publisher;

public interface Publisher <T> {
    /**
        This is the publish method that a publisher will call, calling this method will lead the publisher
        to trigger the publish method in the broker object and hence start publishing data
    */
    public void publish (T item);
}
