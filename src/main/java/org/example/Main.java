package org.example;

import org.example.model.Message;
import org.example.service.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> priorities = initializeSamplePriorities();
        QueueHolder queueHolder = new QueueHolder(priorities);
        OrderedMessageQueueProvider orderedMessageQueueProvider = new OrderedMessageQueueProviderImpl(priorities, queueHolder.getOutputQueue());
        ChannelListener channelListener = new ChannelListenerImpl(priorities, queueHolder.getChannelQueues());

        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(1L, "A", "A payload test".getBytes()));
        channelListener.messageReceived(new Message(2L, "B", "B payload test".getBytes()));
        channelListener.messageReceived(new Message(3L, "C", "C payload test".getBytes()));
        channelListener.messageReceived(new Message(4L, "D", "D payload test".getBytes()));

        queueHolder.batchSendToOutputQueue();
        queueHolder.batchSendToOutputQueue();

        Queue<Message> outputMessages = orderedMessageQueueProvider.getOutQueue();
        while(!outputMessages.isEmpty()){
            Message message = outputMessages.poll();
            long ID = message.getMessageId();
            String channelId = message.getSourceChannelId();

            System.out.printf("Message ID: %s, source channel: %s %n", ID, channelId);

        }
    }

    private static Map<String, Integer> initializeSamplePriorities(){
        return Map.of(
                "A", 1,
                "B", 1,
                "C", 3,
                "D", 5
        );
    }
}