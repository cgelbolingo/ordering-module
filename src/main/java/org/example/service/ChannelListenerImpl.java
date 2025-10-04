package org.example.service;

import org.example.model.Message;

import java.util.Map;
import java.util.Queue;

public class ChannelListenerImpl implements ChannelListener {
    private Map<String, Queue<Message>> channelQueues;
    Map<String, Integer> priorities;

    public ChannelListenerImpl(Map<String, Integer> priorities, Map<String, Queue<Message>> channelQueues){
        this.priorities = priorities;
        this.channelQueues = channelQueues;
    }
    @Override
    public void messageReceived(Message m) {
        Queue<Message> messageQueue = channelQueues.get(m.getSourceChannelId());
        if(messageQueue != null){
            messageQueue.offer(m);
            channelQueues.put(m.getSourceChannelId(), messageQueue);
        }
    }
}
