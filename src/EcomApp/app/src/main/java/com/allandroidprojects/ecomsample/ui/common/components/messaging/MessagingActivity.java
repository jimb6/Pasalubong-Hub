package com.allandroidprojects.ecomsample.ui.common.components.messaging;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.notification.MessagingModelFactory;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.viewmodel.notification.MessagingViewModel;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox.ChatInboxFragment;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.messages.MessagingFragment;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.composer.merchant.main.SectionsPagerAdapter;
import com.allandroidprojects.ecomsample.util.MessageAdapter;

import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    private MessagingViewModel messagingViewModel;
    public static boolean isActivityRunning = false;
    private String userId;
    private Inbox currentInbox;

    private ViewPager viewPager;

//    private Scaledrone scaledrone;
    private ArrayList<Chatroom> messages;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private String TAG = "Firebase Messaging Service-";
    public static Inbox inbox;

    private MessagingFragment messagesFragment;
    private ChatInboxFragment inboxFragment;


    private void initializeViewModel() {
        messagingViewModel = ViewModelProviders.of(this, new MessagingModelFactory(this)).get(MessagingViewModel.class);
    }

    private void setupViewPager(){
        viewPager = findViewById(R.id.view_pager);
        if (viewPager != null) {
            setupFragments(viewPager);
        }
    }

    private void setupFragments(ViewPager pager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        inboxFragment = new ChatInboxFragment();
        adapter.addFragment(inboxFragment, "Inbox");

        messagesFragment = new MessagingFragment();
        adapter.addFragment(messagesFragment, "Messages");

        pager.setAdapter(adapter);
    }

    public ViewPager getViewPager(){
        if(viewPager == null){
            viewPager = findViewById(R.id.view_pager);
            setupViewPager();
        }
        return viewPager;
    }

    public Inbox getCurrentInbox(){
        return this.inbox;
    }

    public void goToMessaging(Inbox item){
        this.inbox = item;
        messagesFragment.setCurrentInbox(inbox);
        getViewPager().setCurrentItem(1);
    }

    public void goToInbox(){
        getViewPager().setCurrentItem(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initializeViewModel();
        setupViewPager();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

}
