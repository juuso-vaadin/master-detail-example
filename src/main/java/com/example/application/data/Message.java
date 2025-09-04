package com.example.application.data;

import java.time.LocalDateTime;

public class Message {
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private String avatarUrl;
    private int userColorIndex;

    public Message() {}

    public Message(String senderName, String content, LocalDateTime timestamp) {
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.userColorIndex = 0;
    }

    public Message(String senderName, String content, LocalDateTime timestamp, String avatarUrl, int userColorIndex) {
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.avatarUrl = avatarUrl;
        this.userColorIndex = userColorIndex;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getUserColorIndex() {
        return userColorIndex;
    }

    public void setUserColorIndex(int userColorIndex) {
        this.userColorIndex = userColorIndex;
    }
}
