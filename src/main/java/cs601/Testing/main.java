package cs601.Testing;

//import cs601.PubSub.Brokers.AsyncOrderedDispatchBroker;
import cs601.PubSub.Brokers.AsyncUnorderedDispatchBroker;
import cs601.PubSub.Brokers.SynchronousOrderedDispatchBroker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class main {

    public static void main (String args[]){
//        SynchronousOrderedDispatchBroker<Integer> broker1 = new SynchronousOrderedDispatchBroker<>();
//        AsyncUnorderedDispatchBroker<Integer> broker1 = new AsyncUnorderedDispatchBroker<>();
        AsyncOrderedDispatchBroker<Integer> broker1 = new AsyncOrderedDispatchBroker<>();

        TestPub<Integer> pub1 = new TestPub<>();
        TestPub<Integer> pub2 = new TestPub<>();
        pub1.addBroker(broker1);
        pub2.addBroker(broker1);

        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        int size = 1000000;
        for (int i = 0; i < size; i++){
            list1.add(i);
            list2.add(size-i);
        }

        System.out.println("Done adding items");

        Sub<Integer> sub1 = new Sub<>(size);
        Sub<Integer> sub2 = new Sub<>(size);
        broker1.subscribe(sub1);
        broker1.subscribe(sub2);

        Thread t1 = new Thread(() -> {
           for (int i = 0; i < list1.size(); i++){
               pub1.publish(list1.get(i));
           }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < list1.size(); i++){
                pub2.publish(list2.get(i));
            }
        });

        t1.start(); t2.start();

        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        broker1.shutdown();

        ArrayList<Integer> a1 = sub1.getNewList();
        ArrayList<Integer> a2 = sub2.getNewList();

        for (int i = 0; i < a1.size(); i++){
            if (a1.get(i) != a2.get(i)){
                System.out.println("Found difference");
                break;
            }
        }

    }
}
