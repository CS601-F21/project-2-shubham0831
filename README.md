Project 2 - Publish/Subscribe Framework
=======================================

### Due - Thursday , October 7, 2021 - 9:55am

For this project, you will implement a message broker framework that will support [publish/subscribe](https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern) functionality. You will practice using the following:

- Concurrency and threads
- Sockets
- Java Generics
- Good design practices

Note, this program does not require a significant amount of code, however you will be heavily graded on your design. You are also expected to spend a significant portion of time comparing the performance of the `Broker` implementations.

This assignment has two parts:

- Part 1 (95%) - Part 1 requires that you implement a program that will be run as a single Java process on one host. You will earn a maximum of 95% credit for completing the part 1 requirements. You *may* take advantage of [Project Resubmission as discussed on the Syllabus](https://cs601-f21.github.io/syllabus.html) if you meet all of the part 1 functionality requirements *even if you do not complete the part 2 functionality requirements*. If you have not completed Part 2 functionality by the deadline, however, you will not earn credit for any resubmission *of Part 2*.
- Part 2 (5%) - Part 2 requires that you implement a program that will allow a `Subscriber` to be executed on a remote host. This will require implementing client/server functionality as part of your solution.

## Requirements (Part 1)

You will implement a framework with the following architecture:

![pubsub](https://github.com/CS601-F21/notes/blob/main/admin/images/pubsub.jpg)

Any number of publishers may publish data by calling the method `publish` on the `Broker`. Any number of subscribers may `subscribe`, and the `Broker` will deliver each message to all subscribers via the `onEvent` method.

### `Broker`

You will implement the following interface for the `Broker`.

```java
public interface Broker<T> {

	/**
	 * Called by a publisher to publish a new item. The 
	 * item will be delivered to all current subscribers.
	 * 
	 * @param item
	 */
	public void publish(T item);
	
	/**
	 * Called once by each subscriber. Subscriber will be 
	 * registered and receive notification of all future
	 * published items.
	 * 
	 * @param subscriber
	 */
	public void subscribe(Subscriber<T> subscriber);
	
	/**
	 * Indicates this broker should stop accepting new
	 * items to be published and shut down all threads.
	 * The method will block until all items that have been
	 * published have been delivered to all subscribers.
	 */
	public void shutdown();
}
```

You will implement the following *three* concrete `Broker` implementations.


#### `SynchronousOrderedDispatchBroker`

The `SynchronousOrderedDispatchBroker` has the following properties:

- **Synchronous** - A newly published item will be *synchronously* delivered to all subscribers. The `publish` method will not return to the publisher until all subscribers have completed the `onEvent` method.
- **Ordered** - The `Broker` guarantees that items from different publishers *may not interleave*. If a publisher is delivering to subscribers the next publisher must block until the first has finished.

<hr/>

#### `AsyncOrderedDispatchBroker`

The `AsyncOrderedDispatchBroker` has the following properties:

- **Asynchronous** - A newly published item will be *asynchronously* delivered to all subscribers. The `publish` method will return to the publisher immediately, and the item will be delivered to the subscribers after the `publish` method completes.
- **Ordered** - The `Broker` guarantees that items from different publishers will be delivered to each subscriber *in the same order*. If any subscriber receives item1 before item2 then all subscribers will receive item1 before item2.

**Hints**

- Consider using a `BlockingQueue` to queue new items as they are published. 
- You may **not** use a `BlockingQueue` implementation from `java.util`, however you may extend and use the implementation presented in class. Consider adding a `poll` method.
- Consider using one additional thread to deliver messages to subscribers in order.

<hr/>

#### `AsyncUnorderedDispatchBroker`

The `AsyncUnorderedDispatchBroker ` has the following properties:

- **Asynchronous** - A newly published item will be *asynchronously* delivered to all subscribers. The `publish` method will return to the publisher immediately, and the item will be delivered to the subscribers after the `publish` method completes.
- **Unordered** - The `Broker` makes no guarantees about the order in which items are delivered to the subscribers. 

**Hints**

- Consider using an instance of `ExecutorService` to asynchronously `execute` jobs to deliver items to subscribers. 

<hr/>

### `Subscriber`

The `Subscriber` interface must be implemented as follows:

```java
public interface Subscriber<T> {

	/**
	 * Called by the Broker when a new item
	 * has been published.
	 * @param item
	 */
	public void onEvent(T item);
	
}
```

### Test Application

The framework described above will be implemented such that it could support a variety of applications. A `Broker` could handle any type of item, which is why we have used Generics.

For your interactive grading demonstration, you will implement an application that will re-sort the Amazon reviews data set. Currently, the data is sorted by type of product (i.e., cell phones, home and kitchen, etc). Your demonstration application will use the publish/subscribe framework to create two new .json files---one with old reviews and one with new reviews.

#### Publishers

1. You will implement at least *two* publishers. 
2. Each publisher must run in a separate thread.
3. Each publisher will be responsible for *one* of the 5-core reviews files from the [Amazon product data](http://jmcauley.ucsd.edu/data/amazon/) dataset. You may choose which files to use for your demonstration, however I recommend Home and Kitchen and Apps for Android.
4. The publisher will parse each review as a separate JSON object and publish the object as a single item.

#### Subscribers

1. You will implement at least *two* subscribers.
2. All subscribers will receive all items. One subscriber will filter items and save to a file only the older reviews and the other will filter items and save to a file only the newer reviews.
3. I recommend using a unix review time of 1362268800 to separate old and new. This should yield roughly 774,000 new reviews and 529,000 old reviews using the data sets recommended above.

#### Brokers

1. You will use this test application to *compare the performance* of the three `Broker` implementations. 
2. During interactive grading you will execute your program using all three implementations and you will need to be prepared to answer questions about why certain `Broker` implementations are faster than others.


### Additional Requirements

1. For all `Broker` implementations, the list of subscribers must be thread safe.
2. Your solution must *accurately* measure the time required to complete delivery of all items to all subscribers. To achieve this you will need to ensure that your `shutdown` method works correctly---waiting until all items have been processed before returning.

### Program Execution

You may decide how your program will be executed, however your design grade will include the following:

1. Naming of your main class.
2. Approach for specifying the input files.
3. Approach for specifying the output files.

Solutions that hard code file paths will receive a deduction.

### External Libraries

The only external libraries you may use for this assignment are [GSON](https://github.com/google/gson) and JUnit. It is not required that you use JUnit for unit testing (but you should make sure you test your code). For this assignment, it is your responsibility to set up the `pom.xml` file correctly.




## Grading Rubric

| Points | Criterion |
| ------ | -------- |  
| Part 1 |  |  
| 15 | **Functionality - Part 1** -  `SynchronousOrderedDispatchBroker` |  
| 20 | **Functionality - Part 1** -  `AsyncUnorderedDispatchBroker` |  
| 20 | **Functionality - Part 1** -  `AsyncOrderedDispatchBroker` |  
| 5 | **Functionality - Part 1** -  Differences in running time for three `Broker`  implementations are adequately explained. |  
| 10 | **Design - Part 1** - Pub/sub framework design requirements are implemented as specified. For team submissions, failure to submit all required assignment components as described above, or submission of inadequate responses for the components described above, will result in a 0 for this criterion. |  
| 5 | **Design - Part 1** - `shutdown` correctly implemented. |  
| 5 | **Design - Part 1** - Thread safety implemented correctly. |  
| 10 | **Design - Part 1** - Test Application design. |  
| 5 | **Design - Part 1** - Meets all style guidelines. |  


## Academic Dishonesty

Any work you submit is expected to be your own original work. If you use any web resources in developing your code you are strongly advised to cite those resources. The only exception to this rule is code that is posted on the class website. The URL of the resource you used in a comment in your code is fine. If I google even a single line of uncited code and find it on the internet you may get a 0 on the assignment or an F in the class. You may also get a 0 on the assignment or an F in the class if your solution is at all similar to that of any other student.

