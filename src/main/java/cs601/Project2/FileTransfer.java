package cs601.Project2;
//import cs601.PubSub.Subscriber.BasicSubscriber;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileTransfer {
    public static void main(String args[]) {
        SimpleStringSubscriber <Integer> Subscriber1 = new SimpleStringSubscriber();
        SimpleStringSubscriber <Integer> Subscriber2 = new SimpleStringSubscriber();

        SimpleStringBroker <Integer> broker = new SimpleStringBroker();

        Subscriber1.subscribeToBroker(broker);
//        Subscriber2.subscribeToBroker(broker);

        SimpleStringPublisher <Integer> publisher1 = new SimpleStringPublisher();
        SimpleStringPublisher <Integer> publisher2 = new SimpleStringPublisher();
        publisher1.addBroker(broker);
        publisher2.addBroker(broker);

        ArrayList<Integer> txt = new ArrayList<>();
        ArrayList<Integer> txt1 = new ArrayList<>();

        for (int i = 0; i < 10; i ++){
            int temp = 10-i-1;
            txt.add(i);
            txt1.add(temp);
        }

//        System.out.println(txt.size());


        ExecutorService tPool = Executors.newFixedThreadPool(4);


        for (int i = 0;  i < txt.size(); i++){
            int finalI = i;

//            This implementation is not working
//            Thread t1 = new Thread(() -> publisher1.publish(txt.get(finalI)));
//            Thread t2 = new Thread(() -> publisher2.publish(txt1.get(finalI)));
//            t1.start();
//            t2.start();
//
//            t2.run();
//            t1.run();
//            This implmentation is working
            tPool.submit( () -> {
               publisher1.publish(txt.get(finalI));
               publisher2.publish(txt1.get(finalI));
            });

        }


        tPool.shutdown();


        try {
            tPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();

        Subscriber1.getI();
    }
}