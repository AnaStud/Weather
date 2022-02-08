package ru.anasoft.weather.messages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.anasoft.weather.R

class MyFCMService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("myLogs", token)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "myChannelId"
        private const val KEY_TITLE = "myTitle"
        private const val KEY_MESSAGE = "myMessage"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data.toMap()
        if(data.isNotEmpty()){
            val myTitle = data[KEY_TITLE]
            val myMessage = data[KEY_MESSAGE]
            if(!myTitle.isNullOrBlank()&&!myMessage.isNullOrBlank())
                pushNotification(myTitle,myMessage)
        }
    }

    private fun pushNotification(title:String,message:String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this,CHANNEL_ID).apply {
            setSmallIcon(R.drawable.bookmark)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_MAX
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Name $CHANNEL_ID"
            val channelDescription = "Description for $CHANNEL_ID"
            val channelPriority = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID,channelName,channelPriority).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }
}