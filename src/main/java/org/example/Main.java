package org.example;

import org.example.model.Message;
import org.example.service.*;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    // MesageReceived and getOutQueue will be running every 5 seconds in separate threads
    // to simulate multiple threads from different callers while supplying and consuming messages at a different rate
    public static void main(String[] args) {
        Map<String, Integer> priorities = initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities, 500);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        ScheduledExecutorService inputDispatcher = Executors.newSingleThreadScheduledExecutor();
        inputDispatcher.scheduleAtFixedRate(() -> {
            channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
            channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
            channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
            channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        }, 0, 1, TimeUnit.SECONDS);

        ScheduledExecutorService outputDispatcher = Executors.newSingleThreadScheduledExecutor();
        outputDispatcher.scheduleAtFixedRate(() -> {
            try {
                queueHolder.batchSendToOutputQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 5, 5, TimeUnit.SECONDS);

        ScheduledExecutorService outputReader = Executors.newSingleThreadScheduledExecutor();
        outputReader.scheduleAtFixedRate(() -> {
            Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
            while(!outputMessages.isEmpty()){
                Message message = outputMessages.poll();
                long ID = message.getMessageId();
                String channelId = message.getSourceChannelId();

                System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);

            }
        }, 6, 5, TimeUnit.SECONDS);

        // Keep main thread alive
        try {
            // Block forever (or until interrupted)
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Each channel is initialized with 5 messages each
    // Sending to output queue is done via thread running every 10 seconds. Reading from Output queue is also done with separate thread.
    // Expected result will be 5 messages in each queue after 5 seconds, after 1 second, first pass to send to output queue will run, resulting in 10 messages.
    // Next pass will send 4 messages to output queue
    // Next pass will send 2 messages to output queue until both queue A and B are depleted
    /*
    public static void main(String[] args) {
        Map<String, Integer> priorities = initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities, 500);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));

        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));

        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));

        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));


        ScheduledExecutorService outputDispatcher = Executors.newSingleThreadScheduledExecutor();
        outputDispatcher.scheduleAtFixedRate(() -> {
            try {
                queueHolder.batchSendToOutputQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 5, 5, TimeUnit.SECONDS);

        ScheduledExecutorService outputReader = Executors.newSingleThreadScheduledExecutor();
        outputReader.scheduleAtFixedRate(() -> {
            Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
            while(!outputMessages.isEmpty()){
                Message message = outputMessages.poll();
                long ID = message.getMessageId();
                String channelId = message.getSourceChannelId();

                System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);

            }
        }, 6, 5, TimeUnit.SECONDS);

        // Keep main thread alive
        try {
            // Block forever (or until interrupted)
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
    */

    //Sending to output queue is manually called. 1 Pass to output queue, expected result will be 10 messages.
    /*
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> priorities = initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities, 500);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));

        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));

        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));

        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));

        queueHolder.batchSendToOutputQueue();

        Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
        while(!outputMessages.isEmpty()){
            Message message = outputMessages.poll();
            long ID = message.getMessageId();
            String channelId = message.getSourceChannelId();

            System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);

        }
     }
     */

    // To test behavior when output queue is full.
    // Check if failed push to outputQueue will retain the failed message. No loss of messages is expected
    /*
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> priorities =initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities, 5);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        channelListener.messageReceived(new Message(11L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(12L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(13L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(14L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(15L, "A", "A payload test".getBytes()));

        channelListener.messageReceived(new Message(21L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(22L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(23L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(24L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(25L, "B", "B payload test".getBytes()));

        channelListener.messageReceived(new Message(31L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(32L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(33L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(34L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(35L, "C", "C payload test".getBytes()));

        channelListener.messageReceived(new Message(41L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(42L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(43L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(44L, "D", "D payload test".getBytes()));
        channelListener.messageReceived(new Message(45L, "D", "D payload test".getBytes()));

        queueHolder.batchSendToOutputQueue();
        queueHolder.batchSendToOutputQueue();

        Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
        while(!outputMessages.isEmpty()) {
            Message message = outputMessages.poll();
            long ID = message.getMessageId();
            String channelId = message.getSourceChannelId();

            System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);
        }
        //Six more runs to verify no messages are lost even if queue is full
        for(int x = 0; x < 6; x++){
            queueHolder.batchSendToOutputQueue();
            outputMessages = orderedMessageQueueProvider.getOutQueue();
            while(!outputMessages.isEmpty()){
                Message message = outputMessages.poll();
                long ID = message.getMessageId();
                String channelId = message.getSourceChannelId();

                System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);
            }
        }
    }
    */

    // To test behavior when output queue is full.
    // Checking if channels keep growing and output queue is still being consumed at a fixed rate.
    /*
    public static void main(String[] args) {
        Map<String, Integer> priorities =initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities, 5);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        ScheduledExecutorService inputDispatcher = Executors.newSingleThreadScheduledExecutor();
        inputDispatcher.scheduleAtFixedRate(() -> {
            channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
            channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
            channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
            channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));
        }, 0, 1, TimeUnit.SECONDS);

        ScheduledExecutorService outputDispatcher = Executors.newSingleThreadScheduledExecutor();
        outputDispatcher.scheduleAtFixedRate(() -> {
            try {
                queueHolder.batchSendToOutputQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 5, 5, TimeUnit.SECONDS);

        ScheduledExecutorService outputReader = Executors.newSingleThreadScheduledExecutor();
        outputReader.scheduleAtFixedRate(() -> {
            Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
            while (!outputMessages.isEmpty()) {
                Message message = outputMessages.poll();
                long ID = message.getMessageId();
                String channelId = message.getSourceChannelId();

                System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);

            }
        }, 6, 5, TimeUnit.SECONDS);

        // Keep main thread alive
        try {
            // Block forever (or until interrupted)
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    */


    private static Map<String, Integer> initializeSamplePriorities(){
        return Map.of(
                "A", 1,
                "B", 1,
                "C", 3,
                "D", 5
        );
    }
}