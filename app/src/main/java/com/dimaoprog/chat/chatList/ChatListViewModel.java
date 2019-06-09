package com.dimaoprog.chat.chatList;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dimaoprog.chat.entity.Chat;

import java.util.List;

public class ChatListViewModel extends ViewModel {

    private MutableLiveData<List<Chat>> liveChatList = new MutableLiveData<>();

    public MutableLiveData<List<Chat>> getLiveChatList() {
        return liveChatList;
    }

    public void setLiveChatList(List<Chat> liveChatList) {
        this.liveChatList.setValue(liveChatList);
    }
}
