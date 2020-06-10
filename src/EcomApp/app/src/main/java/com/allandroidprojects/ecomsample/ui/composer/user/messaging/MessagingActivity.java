package com.allandroidprojects.ecomsample.ui.composer.user.messaging;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.notification.MessagingModelFactory;
import com.allandroidprojects.ecomsample.data.models.Inbox;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.ui.composer.merchant.main.SectionsPagerAdapter;
import com.allandroidprojects.ecomsample.ui.composer.user.messaging.inbox.ChatInboxFragment;
import com.allandroidprojects.ecomsample.ui.composer.user.messaging.messages.MessagingFragment;
import com.allandroidprojects.ecomsample.ui.composer.user.messaging.messages.MessagingViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    private MessagingViewModel messagingViewModel;
    public static boolean isActivityRunning = false;
    private String userId;
    private Inbox currentInbox;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ViewPager viewPager;

//    private Scaledrone scaledrone;
    private ArrayList<Chatroom> messages;
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
        Bundle bundle = new Bundle();

        inboxFragment = new ChatInboxFragment();
        adapter.addFragment(inboxFragment, "Inbox");
        if (getIntent().hasExtra(getString(R.string.message_from_merchant))){
//            getViewPager().setCurrentItem(1);
            String businessID = getIntent().getStringExtra(getString(R.string.message_from_merchant));
            bundle.putString(getString(R.string.message_from_merchant), businessID);
        }
        inboxFragment.setArguments(bundle);


        messagesFragment = new MessagingFragment();
        bundle = new Bundle();
        if (getIntent().hasExtra(getString(R.string.message_with_product_item))){
            Product product = getIntent().getParcelableExtra(getString(R.string.message_with_product_item));
            bundle.putParcelable(getString(R.string.message_with_product_item), product);
            bundle.putString("CONVERSATION_ID", createConversationId(product));
        }
        messagesFragment.setArguments(bundle);
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

    public void goToMessaging(Inbox inbox){
        messagesFragment.setCurrentInbox(inbox);
        getViewPager().setCurrentItem(1);
    }

    public void goToMessagingWithProduct(Product product){
        String conversationId = createConversationId(product);
        messagesFragment.setCurrentInboxWithProduct(conversationId, product);
        getViewPager().setCurrentItem(1);
    }

    private String createConversationId(Product product){
        return user.getUid() + "_" + product.getBusinessOwnerId();
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

        if (getIntent().hasExtra(getString(R.string.message_with_product_item))){
            getViewPager().setCurrentItem(1);
        }

        if (getIntent().hasExtra(getString(R.string.message_from_merchant))){
//            getViewPager().setCurrentItem(1);
            goToInbox();
        }

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
