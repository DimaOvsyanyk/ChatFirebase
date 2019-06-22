package com.dimaoprog.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dimaoprog.chat.chatList.ChatListAdapter;
import com.dimaoprog.chat.chatList.ChatListFragment;
import com.dimaoprog.chat.login.LoginFragment;
import com.dimaoprog.chat.chat.ChatFragment;
import com.dimaoprog.chat.notification.NewMessageListenerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.SignInListener, ChatListAdapter.ChatPickedListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, NewMessageListenerService.class);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
//        authStateListener = firebaseAuth -> {
//            currentUser = firebaseAuth.getCurrentUser();
//            checkUser();
//        };
//            auth.signOut();
            checkUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(serviceIntent);
//        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (authStateListener != null) {
//            auth.removeAuthStateListener(authStateListener);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(serviceIntent);
    }

    @Override
    public void checkUser() {
        if (currentUser != null && currentUser.isEmailVerified()) {
            openChatListFragment();
        } else {
            openLoginFragment();
        }
    }

    public void openLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.newInstance(this))
                .commit();
    }

    public void openChatListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ChatListFragment.newInstance(this))
                .commit();
    }

    @Override
    public void openChatFragment(String chatId, String chatTitle) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ChatFragment.newInstance(chatId, chatTitle))
                .addToBackStack(null)
                .commit();
    }
}
