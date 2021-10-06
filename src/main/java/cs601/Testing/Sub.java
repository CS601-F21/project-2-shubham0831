package cs601.Testing;

import cs601.PubSub.Subscriber.Subscriber;

import java.util.ArrayList;

public class Sub<T> implements Subscriber<T> {
    ArrayList<T> newList = new ArrayList<>();
    int size;

    public Sub (int size){
        this.size = size;
    }
    @Override
    public synchronized void onEvent(T item) {
//        System.out.println("received item " + item);
        newList.add(item);
        if (newList.size() == 2*size){
            System.out.println("received all items");
        }
    }

    public ArrayList<T> getNewList() {
        return newList;
    }
}
