package com.dimaoprog.chat.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimaoprog.chat.MyProgressDialog;
import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ChatFragmentBinding;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private ChatViewModel cViewModel;
    private ChatFragmentBinding binding;
    private static final String CHAT_ID = "chat id";
    private static final String CHAT_TITLE = "chat title";
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
        cViewModel.setChatId(Objects.requireNonNull(getArguments()).getString(CHAT_ID));
        cViewModel.setChatTitle(Objects.requireNonNull(getArguments()).getString(CHAT_TITLE));
        setTitle(cViewModel.getChatTitle());
        MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        cViewModel.getShowProgressDialog().observe(this, show -> progressDialog.showMyProgressDialog(show));
        cViewModel.getMessagesFromDB();
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false);
        binding.setChatViewModel(cViewModel);
        binding.btnSend.setOnClickListener(__ -> cViewModel.sendMessage());

        adapter = new ChatAdapter(cViewModel.getLiveCurrentUserName().getValue());
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        cViewModel.getLiveMessageList().observe(this, messages -> {
            adapter.submitList(messages);
            scrollToLastMessage();
        });
        cViewModel.getLiveCurrentUserName().observe(this, name -> adapter.setCurrentUserName(name));

        return binding.getRoot();
    }

    private void setTitle(String title) {
        Objects.requireNonNull(getActivity()).setTitle(title);
    }

    private void scrollToLastMessage() {
        binding.rvMessages.scrollToPosition(adapter.getItemCount());
        adapter.notifyDataSetChanged();
    }

}
