/**
    Author Name : Shubham Pareek
    Author mail : spareek@dons.usfca.edu
    Class function : Async Ordered Broker
*/
package cs601.PubSub.Brokers;

import cs601.PubSub.Subscriber.Subscriber;
import cs601.Concurrent.CS601BlockingQueue;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
    Explanation :
        We have a list of subscribers that are subscribed to a broker. Everytime a publisher
        publishes a new message, we immediately insert that message into the blocking queue.
        We have a single thread defined, which then gets an item from the blocking queue and
        then dispatches it to each subscriber.
        The use of a single thread is essential as if we used multiple threads, or a threadpool
        we cannot be certain of the order in which the items will arrive to the subscriber and
        whether they are ordered or not.

        Currently, the order in which the broker receives the item is the same order in which all
        the subscribers gets the item.
*/


public class AsyncOrderedDispatchBroker <T> implements Broker <T>{
    //this is the blocking queue we will be using
    CS601BlockingQueue <T> waitList;

    //this is the thread which will get an item from the blocking queue and publish it to each subscriber
    private Thread publisherThread;

    //we will be needing a lock in this class, as we only one thread can put an item in the blocking queue at a time
    private ReentrantReadWriteLock reentrantReadWriteLock;

    //defining the read and write locks respectively
    private Lock readLock;
    private Lock writeLock;

    //is the set of subscriber that subscribe to the broker
    private HashSet<Subscriber> subList;

    //this is boolean which is used to decide whether the broker is still active and accepting new publish requests or not
    private boolean isActive;

    public AsyncOrderedDispatchBroker() {
        //constructor
        //creating a blocking queue with the size at 1000
        this.waitList = new CS601BlockingQueue(100);

        //defining the locks
        this.reentrantReadWriteLock= new ReentrantReadWriteLock();
        readLock = reentrantReadWriteLock.readLock();
        writeLock = reentrantReadWriteLock.writeLock();

        //our sublist will be a hashset of subscribers, this ensures no duplication
        this.subList = new HashSet<>();

        //initially the broker is active
        isActive = true;

        //we use this method to define the thread and the work that it has to do
        createPublishThread();
    };

    private void createPublishThread(){
        //this method creates and defines the purpose of the thread.
        //in this case the thread has to get items from the blockingqueue and
        //publish it to the subscriber

        //we do not start the thread here as we will need to call this method in
        //2 different cases
        //the cases are as following
        //case 1 --> to initialize the thread
        //case 2 --> if for some reason the blocking queue gets empty and the thread is terminated
        //           we cannot reuse a thread and hence we have to redefine and start it again
        publisherThread = new Thread(() -> {
            //the thread will keep on getting an item from the waitlist and then publish it to the
            //respective subscribers
            while (!waitList.isEmpty()){
                T item = waitList.take();
//                System.out.println("Handling item " + item);
                sendItem(item);
            }
        });
    }

    @Override
    public void publish(T item) {
        //publish method, this is the method the publisher calls when it has to publish some items.

        //if the broker is not currently active, we just print out the following message and return back to
        //the caller
        if (!isActive){
            System.out.println("Broker is currently inactive and not accepting publishing requests");
            return;
        }

        //for every item the publisher publishes, we insert that item into the blocking queue
        //eventhough the put method in the blocking queue is synchornous, we still use a write lock
        //since we check the state of the publisherThread as well, and might have to redefine the
        //publisher thread, like if it has gotten terminated for some reason

        //putting items in the blocking queue
        waitList.put(item);

//            System.out.println(publisherThread.getState());

        //since initially the thread is new, we have to start the publisherThread
        if (getThreadState(publisherThread).equals(Thread.State.NEW)){
            publisherThread.start();
        }

        //if for whatever reason, the publisherThread is done publishing all the items in the blocking queue
        //and the blocking queue becomes empty, the thread will have done its job and will be terminated.
        //we cannot restart a terminated thread and hence we have to redefine it and start it again.
        //this if condition checks for the above mentioned condition and redefines and starts the thread again
        else if (getThreadState(publisherThread).equals(Thread.State.TERMINATED)){
            createPublishThread();
            publisherThread.start();
        }
    }


    private Thread.State getThreadState (Thread t) {
        //this method returns the state of any given thread.
        //the use case of this method in the given class is to primarily get the state
        //of the publisher thread
        return t.getState();
    }

    private void sendItem(T item){
        //this is the method in which we actually publish the items to the subscriber, it is different from the publish method
        //which is called by the publisher.
        //it is in this method that we iterate over all the subscribers in the set and send them the message

        //we use a readlock as we will be reading from the subList and we should not allow that to happen if some other thread is writing
        //in the sublist at the same time
        readLock.lock();
        try {
            for (Subscriber subscriber : subList) {
                subscriber.onEvent(item);
            }
        } finally {
            //unlocking the lock
            readLock.unlock();
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        //this is the method which a subscriber calls and this adds the subscriber
        //to the hashset

        //we use a write lock to ensure that two threads cannot read/write and write/write from the subList set at the same time
        //this ensures that the subList set is threadsafe
        writeLock.lock();
        try {
            subList.add(subscriber);
        } finally {
            //unlocking the writeLock
            writeLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        //is the shutdown method, which can be called by the user
        //this method will ensure that the broker not accept any more publish requests.
        isActive = false;
    }
}

