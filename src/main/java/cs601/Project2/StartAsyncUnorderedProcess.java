package cs601.Project2;

import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StartAsyncUnorderedProcess {

    public static void main (String[] args) throws InterruptedException {
        AsyncUnorderedDispatchBroker<Integer> broker1 = new AsyncUnorderedDispatchBroker<>(); //pub1
        AsyncUnorderedDispatchBroker<Integer> broker2 = new AsyncUnorderedDispatchBroker<>(); //pub1



        StringALSubscriber<Integer> subscriber1 = new StringALSubscriber<>();
        StringALSubscriber<Integer> subscriber2 = new StringALSubscriber<>();

        subscriber1.subscribeToBroker(broker1);
        subscriber2.subscribeToBroker(broker2);

        SimpleStringPublisher<Integer> publisher1 = new SimpleStringPublisher<>();
        SimpleStringPublisher<Integer> publisher2 = new SimpleStringPublisher<>();

        publisher1.addBroker(broker1);
        publisher2.addBroker(broker2);

        ArrayList<Integer> txt1 = new ArrayList<>();
        ArrayList<Integer> txt2 = new ArrayList<>();

        for (int i = 1; i < 110000; i++){
            txt1.add(i);
            txt2.add(i+10);
        }

//        ExecutorService tPool = Executors.newFixedThreadPool(40);
//
//        for (int i = 0; i < txt1.size(); i++){
//            int finalI = i;
//            tPool.execute(() -> {
//                publisher1.publish(txt1.get(finalI));
//                publisher2.publish(txt1.get(finalI));
//            });
//        }
//
//        tPool.shutdown();


//        try {
//            tPool.awaitTermination(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < txt1.size(); i++){
//                System.out.println(i);
                publisher1.publish(txt1.get(i));
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < txt2.size(); i++){
//                System.out.println(i);
                publisher2.publish(txt2.get(i));
            }
        });

        t1.start(); t2.start();

        TimeUnit.SECONDS.sleep(20);

//        subscriber1.getI();
//        subscriber2.getI();
        ArrayList<Integer> a1 = subscriber1.getObjList();
        ArrayList<Integer> a2 = subscriber2.getObjList();

//        System.out.println(a1 + " ----- " + a2);

        for (int i = 0; i < a1.size(); i++){
            if (a1.get(i) != a2.get(i)){
                System.out.println("found it");
                break;
            }
        }


    }

}
