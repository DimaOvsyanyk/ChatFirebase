package com.dimaoprog.chat.chat;

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
import android.widget.Toast;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ChatFragmentBinding;
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

public class ChatFragment extends Fragment {

    private ChatViewModel cViewModel;
    private ChatFragmentBinding binding;
    private static final String CHAT_ID = "chat id";
    private static final String CHAT_TITLE = "chat title";
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String currentUserName;
    private DatabaseReference database;

    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;

    public static ChatFragment newInstance(String chatId, String chatTitle) {
        Bundle args = new Bundle();
        args.putString(CHAT_ID, chatId);
        args.putString(CHAT_TITLE, chatTitle);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        Objects.requireNonNull(getActivity()).setTitle(Objects.requireNonNull(getArguments()).getString(CHAT_TITLE));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        setUpProgressDialog();
        setUserName();
        getMessagesFromDB();
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false);
        binding.setChatViewModel(cViewModel);
        binding.btnSend.setOnClickListener(__ -> sendMessage());

        adapter = new ChatAdapter(messageList, currentUserName);
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        cViewModel.getLiveMessageList().observe(this, messages -> adapter.notifyDataSetChanged());
        cViewModel.getLiveCurrentUserName().observe(this, name -> {
            adapter.setCurrentUserName(name);
        });

        return binding.getRoot();
    }

//    private void setUpRecyclerView() {
//        ChatAdapter adapter = new ChatAdapter(messageList, currentUserName);
//        binding.rvMessages.setAdapter(adapter);
//        binding.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
//        cViewModel.getLiveMessageList().observe(this, messages -> adapter.notifyDataSetChanged());
//    }

    private void sendMessage() {
        String newMessageId = database.child("messages/" + Objects.requireNonNull(getArguments()).get(CHAT_ID)).push().getKey();
        if (cViewModel.getNewMessageText() != null) {
            Long time = Calendar.getInstance().getTimeInMillis();
            Message newMessage = new Message(currentUserName, cViewModel.getNewMessageText().get(), time);

            Map<String, Object> postMessage = new HashMap<>();
            postMessage.put("messages/" + Objects.requireNonNull(getArguments()).get(CHAT_ID) + "/" + newMessageId, newMessage.toMap());

            database.updateChildren(postMessage);
            cViewModel.setNewMessageText(null);
            scrollToLastMessage();
            Log.d(LOG, newMessage.toString());
        } else {
            Toast.makeText(getContext(), "Enter message first)", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollToLastMessage() {
        binding.rvMessages.scrollToPosition(adapter.getItemCount() - 1);
        adapter.notifyDataSetChanged();
    }

    private void setUserName() {
        database.child("users/" + currentUser.getUid() + "/user_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                cViewModel.setLiveCurrentUserName(currentUserName);
//                setUpRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, databaseError.getMessage());
            }
        });
    }

    private void getMessagesFromDB() {
        showProgressDialog(true);
        database.child("messages/" + Objects.requireNonNull(getArguments()).get(CHAT_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messageList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    messageList.add(dataSnapshot1.getValue(Message.class));
                }
                cViewModel.setLiveMessageList(messageList);
                scrollToLastMessage();
                showProgressDialog(false);
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

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading......");
        progressDialog.setMessage("Please wait");
    }

}
