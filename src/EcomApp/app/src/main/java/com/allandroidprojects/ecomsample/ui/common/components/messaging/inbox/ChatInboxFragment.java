package com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;

public class ChatInboxFragment extends Fragment {

    private ChatInboxViewModel mViewModel;

    public static ChatInboxFragment newInstance() {
        return new ChatInboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_inbox_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatInboxViewModel.class);
        // TODO: Use the ViewModel
    }



}
