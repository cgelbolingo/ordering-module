package org.example.service;

import org.example.model.Message;

import java.util.Queue;

public interface OrderedMessageQueueProvider {
    Queue<Message> getOutQueue();
}
