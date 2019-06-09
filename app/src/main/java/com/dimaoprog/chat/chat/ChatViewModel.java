package com.dimaoprog.chat.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.dimaoprog.chat.entity.Message;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> liveMessageList = new MutableLiveData<>();
    private ObservableField<String> newMessageText = new ObservableField<>();
    private MutableLiveData<String> liveCurrentUserName = new MutableLiveData<>();


    public ObservableField<String> getNewMessageText() {
        return newMessageText;
    }

    public void setNewMessageText(String newMessageText) {
        this.newMessageText.set(newMessageText);
    }

    public MutableLiveData<List<Message>> getLiveMessageList() {
        return liveMessageList;
    }

    public void setLiveMessageList(List<Message> liveMessageList) {
        this.liveMessageList.setValue(liveMessageList);
    }

    public MutableLiveData<String> getLiveCurrentUserName() {
        return liveCurrentUserName;
    }

    public void setLiveCurrentUserName(String currentUserName) {
        this.liveCurrentUserName.setValue(currentUserName);
    }
}
