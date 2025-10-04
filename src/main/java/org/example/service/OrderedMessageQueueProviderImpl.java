package org.example.service;

import org.example.model.Message;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class OrderedMessageQueueProviderImpl implements OrderedMessageQueueProvider {
    private BlockingQueue<Message> outputQueue;

    public OrderedMessageQueueProviderImpl(Map<String, Integer> priorities, BlockingQueue<Message> outputQueue){
        this.outputQueue = outputQueue;
    }

    @Override
    public Queue<Message> getOutQueue() {
        System.out.println("Output queue size: " + outputQueue.size());
        return outputQueue;
    }
}

