package com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter.ChatInboxAdapter;
import com.allandroidprojects.ecomsample.data.models.Inbox;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ChatInboxFragment extends Fragment {

    private ChatInboxViewModel mViewModel;

//    Variables
    private String TAG = "INBOX";
    private String userID;
    private List<Inbox> inboxes = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    public static ChatInboxFragment newInstance() {
        return new ChatInboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_inbox_fragment, container, false);

        initComponents(view);
        userID = FirebaseAuth.getInstance().getUid();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChatInboxViewModelFactory factory = new ChatInboxViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this, factory).get(ChatInboxViewModel.class);
        // TODO: Use the ViewModel
        getInboxMessage();
    }


    private void getInboxMessage(){
        mViewModel.getAllInbox(userID);
        mViewModel.getAllInboxMutableLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success){
//                Result is
                Timber.d("Fetching Inbox success.");
                if (((Result.Success) result).getData() instanceof Inbox){
                    inboxes.add((Inbox) ((Result.Success) result).getData());
                    adapter.notifyDataSetChanged();
                }
            }else{
                Timber.d("Fetching Inbox %s", ((Result.Error) result).getError().getMessage());
            }
        });
    }


    private void initComponents(View view){
        setupRecyclerView(view);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.inboxRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatInboxAdapter(getActivity(), recyclerView, (ArrayList<Inbox>) inboxes);
        recyclerView.setAdapter(adapter);

    }


}
