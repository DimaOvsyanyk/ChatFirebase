package com.dimaoprog.chat.chatList;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.dimaoprog.chat.databinding.ItemChatBinding;
import com.dimaoprog.chat.entity.Chat;

public class ChatListViewHolder extends RecyclerView.ViewHolder {

    private ItemChatBinding binding;

    public ChatListViewHolder(@NonNull ItemChatBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindView(Chat chat, ChatListAdapter.ChatPickedListener chatPickedListener) {
        binding.setChat(chat);
        itemView.setOnClickListener(__ -> chatPickedListener.openChatFragment(chat.getChatId(), chat.getChat_title()));
    }

}
