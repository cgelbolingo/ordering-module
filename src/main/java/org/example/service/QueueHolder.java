package org.example.service;

import org.example.model.Message;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class QueueHolder {
    private Map<String, Queue<Message>> channelQueues;
    private BlockingQueue<Message> outputQueue;
    private Map<String, Integer> priorities;

    public Map<String, Queue<Message>> getChannelQueues() {
        return channelQueues;
    }

    public BlockingQueue<Message> getOutputQueue() {
        return outputQueue;
    }

    public QueueHolder(Map<String, Integer> priorities){
        channelQueues = new ConcurrentHashMap<>();
        for(String key : priorities.keySet()){
            channelQueues.put(key, new ConcurrentLinkedQueue<>());
        }
        outputQueue = new ArrayBlockingQueue<>(500);
        this.priorities = priorities;
    }

    public void batchSendToOutputQueue() throws InterruptedException {
        for(String key : priorities.keySet()){
            Queue<Message> messageQueue = channelQueues.get(key);
//            System.out.println(messageQueue.size() + " messages found for channel " + key);
            for(int x = 0 ; !messageQueue.isEmpty() && x < priorities.get(key) ; x++){
                Message message = messageQueue.poll();
                if(message != null){
                    outputQueue.offer(message, 1, TimeUnit.SECONDS);
                }
            }
        }
    }
}
