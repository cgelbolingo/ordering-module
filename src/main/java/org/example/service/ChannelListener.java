package org.example.service;

import org.example.model.Message;

public interface ChannelListener {
    void messageReceived(Message m);
}
