package cs601.Project2;

import cs601.Concurrent.CS601BlockingQueue;
import cs601.PubSub.Brokers.AsyncOrderedDispatchBroker;
import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class FileTransfer {
    public static void main(String args[]) throws InterruptedException {
        AsyncOrderedDispatchBroker <Integer> broker = new AsyncOrderedDispatchBroker<>();

        SimpleStringPublisher <Integer> pub1 = new SimpleStringPublisher<>();
        SimpleStringPublisher <Integer> pub2 = new SimpleStringPublisher<>();
        pub1.addBroker(broker);
        pub2.addBroker(broker);

        StringALSubscriber <Integer> sub1 = new StringALSubscriber<>();
        StringALSubscriber <Integer> sub2 = new StringALSubscriber<>();
        sub1.subscribeToBroker(broker);
        sub2.subscribeToBroker(broker);

//        Thread t = new Thread(() -> {
//            int a = 0;
//        });
//
//        t.start();
//        t.start();

        ExecutorService tPool = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 1000000000; i++){
            int finalI = i;
            tPool.submit(() -> {
                pub1.publish(finalI);
                pub2.publish(1000000000+finalI);
            });

//            Thread t = new Thread(() -> {
//               pub1.publish(finalI);
//               pub2.publish(1000000000+finalI);
//            });
        }

        System.out.println("Reached here");


        tPool.shutdown();
        try {
            tPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ArrayList<Integer> s1 = sub1.getObjList();
        ArrayList<Integer> s2 = sub1.getObjList();

        System.out.println(s1.size()); System.out.println(s2.size());

        for (int i = 0; i < s1.size(); i++){
            if (s1.get(i) != s2.get(i)){
                System.out.println("found the mistake");
                break;
            }
        }

        System.out.println("Reached here as well papaaa");
    }
}