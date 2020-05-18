package com.allandroidprojects.ecomsample.data.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.composer.user.accounts.AccountActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.login.LoginActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.registration.RegistrationActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.startup.MainActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.startup.SplashActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.startup.WelcomeActivity;
import com.allandroidprojects.ecomsample.util.UniversalImageLoader;
import com.allandroidprojects.ecomsample.data.models.Chatroom;
import com.allandroidprojects.ecomsample.ui.composer.merchant.messaging.ChatroomActivity;
import com.allandroidprojects.ecomsample.ui.composer.merchant.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.ShopActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.merchant.maps.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagingService extends FirebaseMessagingService {

    private static final int BROADCAST_NOTIFICATION_ID = 160418;
    private String TAG = "Firebase Messaging Service-";
    private final static AtomicInteger c = new AtomicInteger(0);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private int mNumMessagePending = 1;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        initImageLoader();

        String identifydataType = remoteMessage.getData().get(getString(R.string.data_type));
        String title = remoteMessage.getData().get(getString(R.string.data_title));
        String message = remoteMessage.getData().get(getString(R.string.data_message));

        if (isApplicationForeground()) {
            if (identifydataType.equals(getString(R.string.firebase_data_type_admin_broadcast))) {
                sendBroadcastNotification(title, message);
            } else if (identifydataType.equals(getString(R.string.firebase_data_type_chat_broadcast))) {
                sendChat(title, message, remoteMessage);
            }

        }

        if (!isApplicationForeground()) {
            if (identifydataType.equals(getString(R.string.firebase_data_type_admin_broadcast))) {
                sendBroadcastNotification(title, message);
            } else if (identifydataType.equals(getString(R.string.firebase_data_type_chat_broadcast))) {
                sendChat(title, message, remoteMessage);
            }
        }

//        String notifBody = "";
//        String notifTitle = "";
//        String notifData = "";
//
//        try {
//            notifBody = remoteMessage.getNotification().getBody();
//            notifTitle = remoteMessage.getNotification().getTitle();
//            notifData = remoteMessage.getData().toString();
//
//        }catch (NullPointerException e){
//
//            Log.e(TAG, "onMessageReceive - NullPointerException " + e.getMessage());
//        }
//
//        Log.d(TAG, "onMessageReceiveData: " + notifData);
//        Log.d(TAG, "onMessageReceiveTitle: " + notifTitle);
//        Log.d(TAG, "onMessageReceiveBody: " +notifBody);
//
//        sendNotification(notifBody);

//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendChat(String title, String message, RemoteMessage remoteMessage){
        String chatroomId = remoteMessage.getData().get(getString(R.string.chat_room_id));
        Query query= firebaseDatabase.getReference().child(getString(R.string.chat_room_collection)).orderByKey().equalTo(chatroomId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()){
                    DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();

                    Chatroom chatroom = new Chatroom();
                    Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();

                    chatroom.setChatroom_id(objectMap.get(getString(R.string.field_chat_room_id)).toString());
                    chatroom.setChatroom_name(objectMap.get(getString(R.string.field_chat_room_name)).toString());
                    chatroom.setCreator_id(objectMap.get(getString(R.string.field_chat_room_creator_id)).toString());
                    chatroom.setSecurity_level(objectMap.get(getString(R.string.field_chat_room_security_level)).toString());

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    int numMessageSeen = Integer.parseInt(snapshot
                            .child(getString(R.string.field_chat_room_users))
                    .child(userId)
                    .child(getString(R.string.field_chat_room_last_seen_message))
                    .getValue().toString());

                    int numMessage = (int) snapshot.child(getString(R.string.field_chat_room_messages)).child(userId).getChildrenCount();

                    mNumMessagePending = (numMessage - numMessageSeen);
                    sendChatMessageNotification(title, message, chatroom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }

        });
    }


    @Override
    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendChatMessageNotification(String title, String message, Chatroom chatroom){
        int notificationId = buildNotificationId();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_name));

        Intent pendingIntent = new Intent(this, LoginActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.iconpas)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.iconpas))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(message)
                .setSubText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("new message in " + chatroom.getChatroom_name()).setSummaryText(message))
                .setNumber(mNumMessagePending)
                .setColor(getColor(R.color.accent))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "PasalubongHub247";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(0, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendBroadcastNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_name));

        Intent intent = new Intent(this, MessagingActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.iconpas)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.iconpas))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(message)
                .setColor(getColor(R.color.accent))
                .setAutoCancel(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "PasalubongHub247";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(0, builder.build());
//        notificationManager.notify(BROADCAST_NOTIFICATION_ID, builder.build());
    }

    public static int buildNotificationId() {
        return c.incrementAndGet();
    }


    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MessagingWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        try {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            assert user != null;

            Map<String, String> data = new HashMap<>();
            data.put("messaging_token", token);

            firebaseFirestore.collection("USERS").document(user.getUid()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "TOKEN Saved to the database: " + token);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "FAILED TO SAVE TOKEN:  " + e.getMessage());
                }
            });
        } catch (Exception e) {

        }

    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, ChatroomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private boolean isApplicationForeground() {
        boolean isActvityRunning = MessagingActivity.isActivityRunning
                || WelcomeActivity.isActivityRunning
                || SplashActivity.isActivityRunning
                || MainActivity.isActivityRunning
                || LoginActivity.isActivityRunning
                || RegistrationActivity.isActivityRunning
                || ShopActivity.isActivityRunning
                || MapsActivity.isActivityRunning
                || AccountActivity.isActivityRunning;

        return isActvityRunning;
    }

    /**
     * init universal image loader
     */
    private void initImageLoader(){
        UniversalImageLoader imageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

}
