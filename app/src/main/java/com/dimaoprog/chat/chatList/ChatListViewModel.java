package com.dimaoprog.chat.chatList;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimaoprog.chat.entity.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dimaoprog.chat.Constants.LOG;

public class ChatListViewModel extends ViewModel {

    private MutableLiveData<List<Chat>> liveChatList = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressDialog = new MutableLiveData<>();
    private MutableLiveData<String> currentUserName = new MutableLiveData<>();

    private DatabaseReference database;
    private FirebaseUser currentUser;

    public ChatListViewModel() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        getDataFromDB();
    }

    private void getDataFromDB() {
        setShowProgressDialog(true);
        database.child("users/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userChatsId = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("chats").getChildren()) {
                    userChatsId.add(dataSnapshot1.getKey());
                }
                setCurrentUserName(Objects.requireNonNull(dataSnapshot.child("user_name").getValue()).toString());
                getUserChats(userChatsId);
                Log.d(LOG, userChatsId.toString() + " " + dataSnapshot.toString() + " " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
                setShowProgressDialog(false);
            }
        });
    }

    private void getUserChats(List<String> userChatsId) {
        database.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chat> chatList = new ArrayList<>();
                Chat tempChat;
                for (int i = 0; i < userChatsId.size(); i++) {
                    tempChat = dataSnapshot.child(userChatsId.get(i)).getValue(Chat.class);
                    Objects.requireNonNull(tempChat).setChatId(userChatsId.get(i));
                    chatList.add(tempChat);
                }
                setLiveChatList(chatList);
                setShowProgressDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
                setShowProgressDialog(false);
            }
        });
    }

    public MutableLiveData<List<Chat>> getLiveChatList() {
        return liveChatList;
    }

    public void setLiveChatList(List<Chat> liveChatList) {
        this.liveChatList.setValue(liveChatList);
    }

    public MutableLiveData<Boolean> getShowProgressDialog() {
        return showProgressDialog;
    }

    public void setShowProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog.setValue(showProgressDialog);
    }

    public MutableLiveData<String> getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName.setValue(currentUserName);
    }
}
