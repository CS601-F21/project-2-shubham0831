package cs601.Project2;
//import cs601.PubSub.Subscriber.BasicSubscriber;

import cs601.PubSub.Brokers.SynchronousOrderedDispatchBroker;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileTransfer {
    public static void main(String args[]) throws InterruptedException {
        SimpleStringSubscriber <Integer> Subscriber1 = new SimpleStringSubscriber();
        SimpleStringSubscriber <Integer> Subscriber2 = new SimpleStringSubscriber();

        SynchronousOrderedDispatchBroker<Integer> broker = new SynchronousOrderedDispatchBroker<>();

        Subscriber1.subscribeToBroker(broker);
        Subscriber2.subscribeToBroker(broker);

        SimpleStringPublisher <Integer> publisher1 = new SimpleStringPublisher();
        SimpleStringPublisher <Integer> publisher2 = new SimpleStringPublisher();
        SimpleStringPublisher <Integer> publisher3 = new SimpleStringPublisher();
        SimpleStringPublisher <Integer> publisher4 = new SimpleStringPublisher();

        publisher1.addBroker(broker);
        publisher2.addBroker(broker);
        publisher3.addBroker(broker);
        publisher4.addBroker(broker);

        ArrayList<Integer> txt = new ArrayList<>();
        ArrayList<Integer> txt1 = new ArrayList<>();
        ArrayList<Integer> txt2 = new ArrayList<>();
        ArrayList<Integer> txt3 = new ArrayList<>();

        int MAX = 10000000;
//        MAX = 100;

        for (int i = 0; i < MAX; i ++){
            int temp = MAX-i-1;
            txt.add(i);
            txt1.add(temp);
            txt2.add(temp);
            txt3.add(temp);
        }

        ExecutorService tPool = Executors.newFixedThreadPool(40);

        Instant start = Instant.now();


//        for (int i = 0;  i < txt.size(); i++){
//            int finalI = i;
//
////            Single threaded approach, for publishing 4*10000000 data using 4 publishers, takes 10480ms to exit the for loop
////            publisher1.publish(txt.get(finalI));
////            publisher2.publish(txt1.get(finalI));
////            publisher3.publish(txt2.get(finalI));
////            publisher4.publish(txt3.get(finalI));
//
////            ThreadPool implementation, for publishing 4*10000000 data using 4 publishers, takes 3826ms to exit the for loop
////            tPool.submit( () -> {
////               publisher1.publish(txt.get(finalI));
////               publisher2.publish(txt1.get(finalI));
////               publisher3.publish(txt2.get(finalI));
////               publisher4.publish(txt3.get(finalI));
////            });
//        }

//        Using individual threads get back to the main method the fastest at 4ms
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < txt.size(); i++){
                publisher1.publish(txt.get(i));
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < txt.size(); i++){
                publisher2.publish(txt1.get(i));
            }
        });
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < txt.size(); i++){
                publisher3.publish(txt2.get(i));
            }
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < txt.size(); i++){
                publisher4.publish(txt3.get(i));
            }
        });
        t1.start(); t2.start(); t3.start(); t4.start();

        Instant finish = Instant.now();

        long duration = Duration.between(start, finish).toMillis();
        System.out.println("Leaving for loop at " + duration+"ms");

        tPool.shutdown();


        try {
            tPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(30);

        System.out.println();

        Subscriber1.getI();
    }
}