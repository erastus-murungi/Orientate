package com.erastus.orientate.student.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.erastus.orientate.R;

public class ChatPreviewFragment extends Fragment {

    public static ChatPreviewFragment newInstance() {
        return new ChatPreviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_chat_preview, container, false);
        Button goToChatsButton = rootView.findViewById(R.id.button_go_to_chats);
        goToChatsButton.setOnClickListener(view ->
                startActivity(new Intent(getContext(), ChatActivity.class)));

        return rootView;
    }
}