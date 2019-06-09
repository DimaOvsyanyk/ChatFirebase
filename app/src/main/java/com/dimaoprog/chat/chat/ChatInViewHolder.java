package com.dimaoprog.chat.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.dimaoprog.chat.databinding.ItemMessageInBinding;
import com.dimaoprog.chat.entity.Message;

public class ChatInViewHolder extends RecyclerView.ViewHolder {

    private ItemMessageInBinding binding;

    public ChatInViewHolder(@NonNull ItemMessageInBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindView(Message message) {
        binding.setMessage(message);
    }


}
