package com.dimaoprog.chat.chatList;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ItemChatBinding;
import com.dimaoprog.chat.entity.Chat;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {

    private ChatPickedListener chatPickedListener;

    public interface ChatPickedListener{
        void openChatFragment(String chatId, String chatTitle);
    }

    private List<Chat> chatList;

    public ChatListAdapter(List<Chat> chatList, ChatPickedListener chatPickedListener) {
        this.chatList = chatList;
        this.chatPickedListener = chatPickedListener;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_chat, viewGroup, false);
        return new ChatListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder chatListViewHolder, int i) {
        chatListViewHolder.bindView(chatList.get(i), chatPickedListener);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
