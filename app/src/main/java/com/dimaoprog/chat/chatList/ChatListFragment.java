package com.dimaoprog.chat.chatList;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ChatListFragmentBinding;
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

public class ChatListFragment extends Fragment {

    private ChatListViewModel clViewModel;
    private ProgressDialog progressDialog;

    private DatabaseReference database;
    private FirebaseUser currentUser;
    private String currentUserName;

    private ChatListAdapter.ChatPickedListener chatPickedListener;

    private List<Chat> chatList = new ArrayList<>();

    public static ChatListFragment newInstance(ChatListAdapter.ChatPickedListener chatPickedListener) {

        Bundle args = new Bundle();

        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(args);
        fragment.setChatPickedListener(chatPickedListener);
        return fragment;
    }

    public void setChatPickedListener(ChatListAdapter.ChatPickedListener chatPickedListener) {
        this.chatPickedListener = chatPickedListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        clViewModel = ViewModelProviders.of(this).get(ChatListViewModel.class);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        setupProgressDialog();

        ChatListFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_list_fragment, container, false);
        ChatListAdapter adapter = new ChatListAdapter(chatList, chatPickedListener);
        binding.rvChats.setAdapter(adapter);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        clViewModel.getLiveChatList().observe(this, chats -> adapter.notifyDataSetChanged());

        getDataFromDB();

        return binding.getRoot();
    }

    public void getDataFromDB() {
        showProgressDialog(true);
        List<String> userChatsId = new ArrayList<>();

        database.child("users/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("chats").getChildren()) {
                    userChatsId.add(dataSnapshot1.getKey());
                }
                currentUserName = Objects.requireNonNull(dataSnapshot.child("user_name").getValue()).toString();
                setUserChats(userChatsId);

                Log.d(LOG, userChatsId.toString() + " " + dataSnapshot.toString() + " " + dataSnapshot.getChildrenCount());
                Objects.requireNonNull(getActivity()).setTitle(currentUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
                showProgressDialog(false);
            }
        });
    }

    public void setUserChats(List<String> userChatsId) {
        database.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                Chat tempChat;
                for (int i = 0; i < userChatsId.size(); i++) {
                    tempChat = dataSnapshot.child(userChatsId.get(i)).getValue(Chat.class);
                    Objects.requireNonNull(tempChat).setChatId(userChatsId.get(i));
                    chatList.add(tempChat);
                    clViewModel.setLiveChatList(chatList);
                    showProgressDialog(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
                showProgressDialog(false);
            }
        });
    }

    private void showProgressDialog(boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading......");
        progressDialog.setMessage("Please wait");
    }


}
