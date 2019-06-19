package com.dimaoprog.chat.chatList;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimaoprog.chat.entity.Chat;
import com.dimaoprog.chat.entity.ChatUser;
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

public class ChatListViewModel extends ViewModel {

    private MutableLiveData<List<Chat>> liveChatList = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressDialog = new MutableLiveData<>();
    private MutableLiveData<List<ChatUser>> userList = new MutableLiveData<>();
    private String currentUserName;

    private DatabaseReference database;
    private FirebaseUser currentUser;

    public ChatListViewModel() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserName = Objects.requireNonNull(currentUser).getDisplayName();
        database = FirebaseDatabase.getInstance().getReference();
        getDataFromDB();
    }

    private void getDataFromDB() {
        setShowProgressDialog(true);
        database.child("users/" + currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userChatsId = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("chats").getChildren()) {
                    userChatsId.add(dataSnapshot1.getKey());
                }
                getUserChats(userChatsId);
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
                Log.d(LOG, userChatsId.size() + " + " + dataSnapshot.toString());
                for (int i = 0; i < userChatsId.size(); i++) {
                    tempChat = dataSnapshot.child(userChatsId.get(i)).getValue(Chat.class);
                    if (tempChat != null) {
                        tempChat.setChatId(userChatsId.get(i));
                        chatList.add(tempChat);
                    }
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

    public void getAllUsersList() {
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatUser> userList = new ArrayList<>();
                ChatUser newUser;
                String userId;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    userId = dataSnapshot1.getKey();
                    newUser = new ChatUser(userId, dataSnapshot1.child("user_name").getValue(String.class));
                    if (!Objects.requireNonNull(userId).equals(currentUser.getUid())) {
                        userList.add(newUser);
                    }
                }
                setUserList(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
            }
        });
    }

    public void deleteChat(Chat chat) {
        String chatId = chat.getChatId();
        database.child("members").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> deletionList = new HashMap<>();
                deletionList.put("chats/" + chatId, null);
                deletionList.put("members/" + chatId, null);
                deletionList.put("messages/" + chatId, null);

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    deletionList.put("users/" + dataSnapshot1.getKey() + "/chats/" + chatId, null);
                }
                database.updateChildren(deletionList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
            }
        });
    }

    public void addNewChat(ChatUser chatUser) {
        String newChatId = database.child("chats").push().getKey();
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        Chat newChat = new Chat(currentUserName + " + " + chatUser.getUserName(), " ", currentTime);
        Map<String, Object> postNewChat = new HashMap<>();
        postNewChat.put("chats/" + newChatId, newChat.toMap());
        database.updateChildren(postNewChat);

        database.child("members").child(newChatId).child(currentUser.getUid()).setValue(true);
        database.child("members").child(newChatId).child(chatUser.getUserId()).setValue(true);

        database.child("users").child(currentUser.getUid()).child("chats").child(newChatId).setValue(true);
        database.child("users").child(chatUser.getUserId()).child("chats").child(newChatId).setValue(true);

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

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public MutableLiveData<List<ChatUser>> getUserList() {
        return userList;
    }

    public void setUserList(List<ChatUser> userList) {
        this.userList.setValue(userList);
    }
}
