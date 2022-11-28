package com.mealer.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mealer.ui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public static final String TAG = "NotificationService" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    String client = "wFv7yMk5Khf4YOS8LV4RAj0vQCC2";
    DatabaseReference user;

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Log.d(TAG, previousChildName + " ");
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            for(DataSnapshot child : snapshot.child("orders").getChildren()){
                Order order = child.getValue(Order.class);
                String cook = null;
                if(snapshot.getValue(CookUser.class)!=null) {
                    cook = snapshot.getValue(CookUser.class).getFirstName();
                }
                if (order != null && order.getOrderFrom().equals(client)) {
                    order.setOrderId(child.getKey());
                    if(!order.getOrderStatus().equals("pending") && order.getNotifications()==0) {
                        createNotification(
                                order.getOrderStatus(),
                                order.getOrderItem().getItemName(),
                                cook);
                        order.notified(user
                                .child(Objects.requireNonNull(snapshot.getKey())) // cookId
                                .child("orders")
                                .child(Objects.requireNonNull(child.getKey())));
                    }
                }

            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG , "onStartCommand" ) ;
        super.onStartCommand(intent , flags , startId);

        user = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        // user.addValueEventListener(actionListener);
        user.addChildEventListener(childEventListener);

        return START_STICKY ;
    }

    @Override
    public void onCreate () {
        Log.e( TAG , "onCreate");
    }

    @Override
    public void onDestroy () {
        Log.e(TAG, "onDestroy");
        super.onDestroy() ;
    }

    private void createNotification (String status, String order, String cook) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
        mBuilder.setContentTitle( "Order " + status ) ;
        if(status.equals("accepted") && cook!=null) {
            mBuilder.setContentText("Your order for " + order + " was " + status + ". " + cook +
                " will call to inform you when your order is ready.");
            mBuilder.setTicker("Your order for " + order + " was " + status + ". " + cook +
                    " will call to inform you when your order is ready.");
        }else{
            mBuilder.setContentText("Your order for " + order + " was " + status + ".");
            mBuilder.setTicker("Your order for " + order + " was " + status + ".");
        }
        mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }
}
