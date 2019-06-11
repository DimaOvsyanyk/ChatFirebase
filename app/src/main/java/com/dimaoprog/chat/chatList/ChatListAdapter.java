package com.dimaoprog.chat.chatList;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.ItemChatBinding;
import com.dimaoprog.chat.entity.Chat;

public class ChatListAdapter extends ListAdapter<Chat, ChatListViewHolder> {

    private ChatPickedListener chatPickedListener;

    protected ChatListAdapter(ChatPickedListener chatPickedListener) {
        super(DIFF_UTIL);
        this.chatPickedListener = chatPickedListener;
    }

    public interface ChatPickedListener{
        void openChatFragment(String chatId, String chatTitle);
    }

    private final static DiffUtil.ItemCallback<Chat> DIFF_UTIL = new DiffUtil.ItemCallback<Chat>() {
        @Override
        public boolean areItemsTheSame(@NonNull Chat chat, @NonNull Chat t1) {
            return chat.getChatId().equals(t1.getChatId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Chat chat, @NonNull Chat t1) {
            return chat.getChatId().equals(t1.getChatId()) &
                    chat.getChat_title().equals(t1.getChat_title()) &
                    chat.getLast_message().equals(t1.getLast_message()) &
                    chat.getTime_stamp() == t1.getTime_stamp();
        }
    };

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_chat, viewGroup, false);
        return new ChatListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder chatListViewHolder, int i) {
        chatListViewHolder.bindView(getItem(i), chatPickedListener);
    }
}
