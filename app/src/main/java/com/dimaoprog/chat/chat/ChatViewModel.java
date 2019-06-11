package com.dimaoprog.chat.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimaoprog.chat.entity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dimaoprog.chat.Constants.LOG;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> liveMessageList = new MutableLiveData<>();
    private ObservableField<String> newMessageText = new ObservableField<>();
    private MutableLiveData<String> liveCurrentUserName = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressDialog = new MutableLiveData<>();

    private String chatId;
    private String chatTitle;


    private FirebaseUser currentUser;
    private String currentUserName;
    private DatabaseReference database;

    public ChatViewModel() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        setUserName();
    }

    private void setUserName() {
        database.child("users/" + currentUser.getUid() + "/user_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                setLiveCurrentUserName(currentUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
            }
        });
    }

    public void sendMessage() {
        String newMessageId = database.child("messages/" + chatId).push().getKey();
        if (getNewMessageText() != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            Message newMessage = new Message(currentUserName, getNewMessageText().get(), time);

            Map<String, Object> postMessage = new HashMap<>();
            postMessage.put("messages/" + chatId + "/" + newMessageId, newMessage.toMap());

            database.updateChildren(postMessage);
            setNewMessageText(null);
            Log.d(LOG, newMessage.toString());
        } else {
            Log.d(LOG, "Enter message first)");
        }
    }


    public void getMessagesFromDB() {
        setShowProgressDialog(true);
        database.child("messages/" + chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    messageList.add(dataSnapshot1.getValue(Message.class));
                }
                setLiveMessageList(messageList);
                setShowProgressDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
                setShowProgressDialog(false);
            }
        });
    }


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

    public MutableLiveData<Boolean> getShowProgressDialog() {
        return showProgressDialog;
    }

    public void setShowProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog.setValue(showProgressDialog);
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
}
