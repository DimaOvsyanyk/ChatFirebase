package com.dimaoprog.chat.chatList;

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

import com.dimaoprog.chat.MyProgressDialog;
import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ChatListFragmentBinding;
import java.util.Objects;

public class ChatListFragment extends Fragment {

    private ChatListAdapter.ChatPickedListener chatPickedListener;

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
        ChatListFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_list_fragment, container, false);
        MyProgressDialog dialog = new MyProgressDialog(getContext());
        ChatListViewModel clViewModel = ViewModelProviders.of(this).get(ChatListViewModel.class);
        clViewModel.getShowProgressDialog().observe(this, show -> dialog.showMyProgressDialog(show));
        clViewModel.getCurrentUserName().observe(this, this::setTitle);

        ChatListAdapter adapter = new ChatListAdapter(chatPickedListener);
        binding.rvChats.setAdapter(adapter);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        clViewModel.getLiveChatList().observe(this, adapter::submitList);

        return binding.getRoot();
    }

    private void setTitle(String userName) {
        Objects.requireNonNull(getActivity()).setTitle(userName);
    }
}
