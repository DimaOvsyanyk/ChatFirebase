package com.dimaoprog.chat.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.dimaoprog.chat.databinding.ItemMessageOutBinding;
import com.dimaoprog.chat.entity.Message;

public class ChatOutViewHolder extends RecyclerView.ViewHolder {

    private ItemMessageOutBinding binding;

    public ChatOutViewHolder(@NonNull ItemMessageOutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindView(Message message) {
        binding.setMessage(message);
    }


}
