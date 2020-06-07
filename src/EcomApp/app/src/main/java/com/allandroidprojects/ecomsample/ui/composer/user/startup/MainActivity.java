package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.interfaces.IDataHelper;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.ordermanagement.BuyProductActivity;
import com.allandroidprojects.ecomsample.ui.common.components.EmptyActivity;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.ChatroomActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.accounts.AccountActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.login.LoginActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.merchant.maps.MapsActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.product.CartListActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.product.ProductListFragment;
import com.allandroidprojects.ecomsample.ui.composer.user.product.SearchResultActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.product.WishlistActivity;
import com.allandroidprojects.ecomsample.util.NotificationCountSetClass;
import com.allandroidprojects.ecomsample.util.ProductCategory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnConnectionFailedListener, IDataHelper {

    public static int notificationCountCart = 0;
    private SimpleDraweeView user_profile;
    private TextView user_displayName;
    private TextView user_email;
    private View progressBar, noProductView;
    static ViewPager viewPager;
    static TabLayout tabLayout;
    private GoogleSignInClient googleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private NavigationView navigationView;
    private LoggedInUser user;
    private FirebaseUser firebaseUser;
    public static boolean isActivityRunning = false;
    private String BASE_URL = "";
    private String mServerKey = "";

    public IDataHelper getInstance() {
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        intDrawerLayout();

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        progressBar = findViewById(R.id.hubProgressBar);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }

        checkAuthenticationState();
        initGoogleSignInClient();
        setUserPreferences();
        initFCMToken();
        getPendingIntent();

    }

    private void intDrawerLayout() {

        int[] icons = {
                R.string.fa_tags_solid,
                R.string.fa_pizza_slice_solid,
                R.string.fa_boxes_solid,
                R.string.fa_tshirt_solid,
                R.string.fa_book_solid,
                R.string.fa_ellipsis_h_solid,
                R.string.fa_map_solid
        };
        renderMenuIcons(navigationView.getMenu(), icons, true, false);

        int[] iconsSubmenu = {
                R.string.fa_archive_solid,
                R.string.fa_comments_solid,
                R.string.fa_heart_solid,
                R.string.fa_cog_solid,
                R.string.fa_sign_out_alt_solid,
        };

        renderMenuIcons(navigationView.getMenu().getItem(7).getSubMenu(), iconsSubmenu, true, false);
    }

    /**
     * Looping through menu icons are applying font drawable
     */
    private void renderMenuIcons(Menu menu, int[] icons, boolean isSolid, boolean isBrand) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (!menuItem.hasSubMenu()) {
                FontDrawable drawable = new FontDrawable(this, icons[i], isSolid, isBrand);
                drawable.setTextColor(ContextCompat.getColor(this, R.color.jet));
                drawable.setTextSize(22);
                menu.getItem(i).setIcon(drawable);
            }
        }
    }


    private void checkAuthenticationState() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri photo = firebaseUser.getPhotoUrl() == null ?
                Uri.parse("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQMX7u2vT0EXHHAobJCKBcqwJAfFKWpgdZ59McdkiYVyVeU_27H") :
                firebaseUser.getPhotoUrl();
        user = new LoggedInUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), photo);
        if (firebaseUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void getPendingIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.intent_chatroom))) {
            Chatroom chatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom));
            Intent chatroomIntent = new Intent(MainActivity.this, ChatroomActivity.class);
            chatroomIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
            startActivity(chatroomIntent);
        } else if (intent.hasExtra(getString(R.string.intent_order_reference))) {
            if (intent.getStringExtra(getString(R.string.intent_order_reference)).equals("Update")){
                startActivity(new Intent(MainActivity.this, BuyProductActivity.class));
            }else{
                String reference = intent.getStringExtra(getString(R.string.intent_order_reference));
                Intent orderIntent = new Intent(MainActivity.this, AccountActivity.class);
                orderIntent.putExtra(getString(R.string.intent_order_reference), reference);
                startActivity(orderIntent);
            }

        }

    }

    private void initFCMToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseInstanceId.getInstance().getId();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d("MAIN ACTIVITY", "sendRegistrationToServer: sending token to server: Firebase Database" + token);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child(getString(R.string.dbnode_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_messaging_token))
                        .setValue(token);

                Log.d("MAIN ACTIVITY", "sendRegistrationToServer: sending token to server: Firestore " + token);
                Map<String, Object> data = new HashMap<>();
                data.put("messaging_token", token);

                db.collection("users").document(user.getUserId()).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "TOKEN Saved to the database: " + token);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "FAILED TO SAVE TOKEN:  " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            Log.e("TAG", "FAILED TO SAVE TOKEN:  " + e.getMessage());
        }
    }

    private LoggedInUser getUserFromIntent() {
        return (LoggedInUser) getIntent().getSerializableExtra("USER");
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void setUserPreferences() {
        View header = navigationView.getHeaderView(0);
        user_profile = header.findViewById(R.id.profileicon);

        if (firebaseUser.getPhotoUrl() != null) {
            user_profile.setImageURI(firebaseUser.getPhotoUrl());
        } else {
            ImageView iconHeader = navigationView.getHeaderView(0).findViewById(R.id.profileicon);
            FontDrawable drawable = new FontDrawable(this, R.string.fa_font_awesome, false, true);
            drawable.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            drawable.setTextSize(50);
            iconHeader.setImageDrawable(drawable);
        }
        user_displayName = header.findViewById(R.id.tvAccountName);
        user_displayName.setText(user.getDisplayName());
        user_email = header.findViewById(R.id.tvEmail);
        user_email.setText(user.getEmail());
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        checkAuthenticationState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCountCart);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(MainActivity.this, SearchResultActivity.class));
            return true;
        } else if (id == R.id.action_cart) {

           /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
            startActivity(new Intent(MainActivity.this, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
            return true;
        } else {
            startActivity(new Intent(MainActivity.this, EmptyActivity.class));

        }
        return super.onOptionsItemSelected(item);
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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();

        StartupFragment startup = new StartupFragment();
        bundle.putString("type", "Latest");
        startup.setArguments(bundle);
        adapter.addFragment(startup, "Welcome To Pasalubong Hub");

        ProductListFragment fragment = new ProductListFragment(this);
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.SWEETS.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.SWEETS.getValue());

        fragment = new ProductListFragment(this);
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.GOODS.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.GOODS.getValue());

        fragment = new ProductListFragment(this);
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.CLOTHING.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.CLOTHING.getValue());

        fragment = new ProductListFragment(this);
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.DECORATION.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.DECORATION.getValue());

        fragment = new ProductListFragment(this);
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.SOUVENIR.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.SOUVENIR.getValue());

        viewPager.setAdapter(adapter);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_item1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_item2) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_item3) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.nav_item4) {
            viewPager.setCurrentItem(3);
        } else if (id == R.id.nav_item5) {
            viewPager.setCurrentItem(4);
        } else if (id == R.id.nav_item6) {
            viewPager.setCurrentItem(5);
        } else if (id == R.id.map) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.my_orders) {
            startActivity(new Intent(MainActivity.this, BuyProductActivity.class));
        } else if (id == R.id.my_wishlist) {
            startActivity(new Intent(MainActivity.this, WishlistActivity.class));
        } else if (id == R.id.messages) {
            startActivity(new Intent(MainActivity.this, MessagingActivity.class));
//        } else if (id == R.id.my_rewards) {
//            startActivity(new Intent(MainActivity.this, EmptyActivity.class));
        } else if (id == R.id.my_account) {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(MainActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            startActivity(new Intent(MainActivity.this, EmptyActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Cannot Logout. Please check your connection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataComplete(boolean hasContents) {

    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
