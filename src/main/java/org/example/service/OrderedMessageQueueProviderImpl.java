package org.example.service;

import org.example.model.Message;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderedMessageQueueProviderImpl implements OrderedMessageQueueProvider {
    private BlockingQueue<Message> outputQueue;
    private Map<String, Integer> priorities;

    public OrderedMessageQueueProviderImpl(Map<String, Integer> priorities, BlockingQueue<Message> outputQueue){
        this.outputQueue = outputQueue;
        this.priorities = priorities;
    }

    @Override
    public Queue<Message> getOutQueue() {
        System.out.println("Output queue size: " + outputQueue.size());
        return outputQueue;
    }
}

