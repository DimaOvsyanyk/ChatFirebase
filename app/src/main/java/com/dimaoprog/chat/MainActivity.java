package com.dimaoprog.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dimaoprog.chat.chatList.ChatListAdapter;
import com.dimaoprog.chat.chatList.ChatListFragment;
import com.dimaoprog.chat.login.LoginFragment;
import com.dimaoprog.chat.chat.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.SignInListener, ChatListAdapter.ChatPickedListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
//        authStateListener = firebaseAuth -> {
//            currentUser = firebaseAuth.getCurrentUser();
//            checkUser();
//        };
        if (savedInstanceState == null) {
//            auth.signOut();
            checkUser();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
