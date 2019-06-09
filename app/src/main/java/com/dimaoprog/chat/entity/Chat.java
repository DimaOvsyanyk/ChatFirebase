package com.dimaoprog.chat.entity;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    private String chat_title;
    private String last_message;
    private long time_stamp;
    private String chatId;

    public Chat() {

    }

    public Chat(String chat_title, String last_message, long time_stamp) {
        this.chat_title = chat_title;
        this.last_message = last_message;
        this.time_stamp = time_stamp;
    }

    public String getChat_title() {
        return chat_title;
    }

    public void setChat_title(String chat_title) {
        this.chat_title = chat_title;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chat_title='" + chat_title + '\'' +
                ", last_message='" + last_message + '\'' +
                ", time_stamp=" + time_stamp +
                ", chatId='" + chatId + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("chat_title", chat_title);
        result.put("last_message", last_message);
        result.put("time_stamp", time_stamp);
        return result;
    }
}
