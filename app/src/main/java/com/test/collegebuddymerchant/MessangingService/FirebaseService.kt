package com.test.collegebuddymerchant.MessangingService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.test.collegebuddymerchant.MainActivity
import com.test.collegebuddymerchant.R
import kotlin.random.Random

private const val CHANNEL_ID="my_channel";

class FirebaseService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.notification!=null){
            Log.d("subhu", message.notification!!.title.toString())
        }

        //Log.d("subhu",message.data["title"].toString()+" "+message.data["message"])

        val intent =Intent(this, MainActivity::class.java)
        val notificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        var notification :Notification

        if(message.notification!=null){
            notification= NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(message.notification!!.title)
                    .setContentText(message.notification!!.body)
                    .setSmallIcon(R.drawable.notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
        }else{
            notification= NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(message.data["title"])
                    .setContentText(message.data["message"])
                    .setSmallIcon(R.drawable.notification)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
        }

        notificationManager.notify(notificationID, notification)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description= "My channel description"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}