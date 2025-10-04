package org.example.model;

public class Message {
    private long messageId;
    private String sourceChannelId;
    private byte[] payload;

    public Message(long messageId, String sourceChannelId, byte[] payload){
        this.messageId = messageId;
        this.sourceChannelId = sourceChannelId;
        this.payload = payload;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSourceChannelId() {
        return sourceChannelId;
    }

    public void setSourceChannelId(String sourceChannelId) {
        this.sourceChannelId = sourceChannelId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
