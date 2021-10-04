package cs601.PubSub.Brokers;

/*
    Once a thread is terminated, we cannot start it again, and we have to create a new thread with similar properties and workload
*/

import cs601.PubSub.Subscriber.Subscriber;
import cs601.Concurrent.CS601BlockingQueue;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class AsyncOrderedDispatchBroker <T> implements Broker <T>{
    CS601BlockingQueue <T> waitList;
    private Thread pubThread;
    private ReentrantReadWriteLock reentrantReadWriteLock;
    private Lock readLock;
    private Lock writeLock;
    private HashSet<Subscriber> subList;

    public AsyncOrderedDispatchBroker() {
        this.waitList = new CS601BlockingQueue(100000000);
        this.reentrantReadWriteLock= new ReentrantReadWriteLock();
        readLock = reentrantReadWriteLock.readLock();
        writeLock = reentrantReadWriteLock.writeLock();
        this.subList = new HashSet<>();

        defineThreadJob();
    };

    private void defineThreadJob (){
        pubThread = new Thread(() -> {
            while (!waitList.isEmpty()){
                T item = waitList.take();
//                System.out.println("Took item " + item);
                sendItem(item);
            }
        });
    }

    public CS601BlockingQueue getWaitList() {
        return waitList;
    }

    @Override
    public void publish(T item) {
        writeLock.lock();
        readLock.lock();
        try {
//            System.out.println(item);
            waitList.put(item);
//            System.out.println("Thread " + Thread.currentThread().getName() + " --> " + getThreadState(pubThread));
            if (getThreadState(pubThread).equals(Thread.State.NEW)){
                pubThread.start();
            }
            else if (getThreadState(pubThread).equals(Thread.State.TERMINATED)){
                //once a thread is terminated, we cannot start it again, hence we call this method, which
                //creates a new thread and makes it do the same work as the previous one.
                defineThreadJob();
                pubThread.start();
            }
        }finally {
            writeLock.unlock();
            readLock.unlock();
        }

    }

    public Thread.State getThreadState (Thread t) {
//        System.out.println(t.getState());
        return t.getState();
    }

    private void sendItem(T item){
        for (Subscriber subscriber : subList){
            subscriber.onEvent(item);
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subList.add(subscriber);
    }

    @Override
    public void shutdown() {

    }
}
