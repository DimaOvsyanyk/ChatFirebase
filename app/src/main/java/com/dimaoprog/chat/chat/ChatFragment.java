package com.dimaoprog.chat.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {

    private static final int SPEECH_RESULT_CODE = 1;

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
        cViewModel.getShowProgressDialog().observe(getViewLifecycleOwner(), show -> progressDialog.showMyProgressDialog(show));
        cViewModel.getMessagesFromDB();
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false);
        binding.setChatViewModel(cViewModel);

        adapter = new ChatAdapter(cViewModel.getCurrentUserName());
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        cViewModel.getLiveMessageList().observe(this, messages -> {
            adapter.submitList(messages);
            scrollToLastMessage(messages.size() - 1);
        });
        binding.btnVoiceToText.setOnClickListener(__ -> takeVoiceMessageToText());
        return binding.getRoot();
    }

    private void takeVoiceMessageToText() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech prompt");
        try {
            startActivityForResult(voiceIntent, SPEECH_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Speech not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_RESULT_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                cViewModel.setNewMessageText(result.get(0));
            }
        }
    }

    private void setTitle(String title) {
        Objects.requireNonNull(getActivity()).setTitle(title);
    }

    private void scrollToLastMessage(int index) {
        binding.rvMessages.scrollToPosition(index);
    }

}
