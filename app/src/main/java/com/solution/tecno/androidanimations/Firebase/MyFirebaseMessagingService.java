package com.solution.tecno.androidanimations.Firebase;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.solution.tecno.androidanimations.utils.Utils;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //getting the title and the body
        //String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getData().get("body");
        String title= remoteMessage.getData().get("title");

        new Utils().showNotification(getApplicationContext(),body,title);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}