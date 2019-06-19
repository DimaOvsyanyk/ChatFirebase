package com.dimaoprog.chat.chatList;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimaoprog.chat.MyProgressDialog;
import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ChatListFragmentBinding;
import com.dimaoprog.chat.entity.Chat;
import com.dimaoprog.chat.entity.ChatUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListFragment extends Fragment {

    private ChatListAdapter.ChatPickedListener chatPickedListener;
    private ChatListViewModel clViewModel;
    private ChatListAdapter adapter;

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
        clViewModel = ViewModelProviders.of(this).get(ChatListViewModel.class);
        clViewModel.getShowProgressDialog().observe(getViewLifecycleOwner(), dialog::showMyProgressDialog);
        setTitle(clViewModel.getCurrentUserName());

        adapter = new ChatListAdapter(chatPickedListener);
        binding.rvChats.setAdapter(adapter);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        clViewModel.getLiveChatList().observe(getViewLifecycleOwner(), adapter::submitList);
        clViewModel.getUserList().observe(getViewLifecycleOwner(), userList -> {
            if (userList.size() > 0) {
                addChatDialog(userList);
            }
        });

        binding.fabAddChat.setOnClickListener(__ -> clViewModel.getAllUsersList());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                deleteConfirmDialog(adapter.getItem(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(binding.rvChats);

        return binding.getRoot();
    }

    private void setTitle(String userName) {
        Objects.requireNonNull(getActivity()).setTitle(userName);
    }

    private void deleteConfirmDialog(Chat chat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Delete this chat?")
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> clViewModel.deleteChat(chat))
                .setNegativeButton(R.string.cancel, (dialog, which) -> adapter.notifyDataSetChanged()).show();
    }

    private int userPosition;

    private void addChatDialog(List<ChatUser> userList) {
        userPosition = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Choose user to chat with")
                .setCancelable(false)
                .setSingleChoiceItems(getUserNames(userList), 0, (dialog, which) -> userPosition = which)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    clViewModel.addNewChat(userList.get(userPosition));
                    clViewModel.setUserList(new ArrayList<>());
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> clViewModel.setUserList(new ArrayList<>())).show();
    }

    private String[] getUserNames(List<ChatUser> userList) {
        String[] userNames = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            userNames[i] = userList.get(i).getUserName();
        }
        return userNames;
    }
}
