package com.dimaoprog.chat.entity;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    private String author;
    private String message;
    private long time_stamp;

    public Message() {

    }

    public Message(String author, String message, long time_stamp) {
        this.author = author;
        this.message = message;
        this.time_stamp = time_stamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "author='" + author + '\'' +
                ", message='" + message + '\'' +
                ", time_stamp=" + time_stamp +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("message", message);
        result.put("time_stamp", time_stamp);
        return result;
    }
}
