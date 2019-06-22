package com.dimaoprog.chat.notification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.dimaoprog.chat.Constants.LOG;

public class NewMessageListenerService extends Service {

    private NotificationManager manager;
    private FirebaseUser currentUser;
    private DatabaseReference database;
    private Context context;
    private List<String> userChatsId = new ArrayList<>();
    private ValueEventListener eventListener;

    public NewMessageListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        context = this;
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                manager.notify(1, Notifications.getNewMessageNotification(context, manager));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        getUserChatsId();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < userChatsId.size(); i++) {
            database.child("chats").child(userChatsId.get(i)).removeEventListener(eventListener);
        }
    }

    private void getUserChatsId() {
        database.child("users/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("chats").getChildren()) {
                    userChatsId.add(dataSnapshot1.getKey());
                }
                listenForNewMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
            }
        });
    }

    private void listenForNewMessages() {
        for (int i = 0; i < userChatsId.size(); i++) {
            database.child("chats").child(userChatsId.get(i)).addValueEventListener(eventListener);
        }
    }
}
