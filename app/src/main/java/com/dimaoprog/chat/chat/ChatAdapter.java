package com.dimaoprog.chat.chat;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ItemMessageInBinding;
import com.dimaoprog.chat.databinding.ItemMessageOutBinding;
import com.dimaoprog.chat.entity.Message;

public class ChatAdapter extends ListAdapter<Message, RecyclerView.ViewHolder> {

    private String currentUserName;
    private static final int IN_MESSAGE = 0;
    private static final int OUT_MESSAGE = 1;

    protected ChatAdapter(String currentUserName) {
        super(DIFF_CALLBACK);
        this.currentUserName = currentUserName;
    }

    private static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message message, @NonNull Message t1) {
            return message.getTime_stamp() == t1.getTime_stamp();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message message, @NonNull Message t1) {
            return message.getTime_stamp() == t1.getTime_stamp() &
                    message.getAuthor().equals(t1.getAuthor()) &
                    message.getMessage().equals(t1.getMessage());
        }
    };

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getAuthor().equals(currentUserName)) {
            return OUT_MESSAGE;
        } else {
            return IN_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == IN_MESSAGE) {
            ItemMessageInBinding bindingIn = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_in, viewGroup, false);
            return new ChatInViewHolder(bindingIn);
        } else {
            ItemMessageOutBinding bindingOut = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_out, viewGroup, false);
            return new ChatOutViewHolder(bindingOut);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case IN_MESSAGE:
                ChatInViewHolder holderIn = (ChatInViewHolder) viewHolder;
                holderIn.bindView(getItem(i));
                break;
            case OUT_MESSAGE:
                ChatOutViewHolder holderOut = (ChatOutViewHolder) viewHolder;
                holderOut.bindView(getItem(i));
                break;
        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull ChatInViewHolder chatInViewHolder, int i) {
//        chatInViewHolder.bindView(messageList.get(i));
//    }

}
