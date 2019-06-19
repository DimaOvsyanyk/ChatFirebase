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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        if (savedInstanceState == null) {
//            auth.signOut();
            checkUser();
        }
    }

    @Override
    public void checkUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
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
