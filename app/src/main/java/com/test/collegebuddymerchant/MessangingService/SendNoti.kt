package com.test.collegebuddymerchant.MessangingService

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class sendNoti {

    val TOPIC="/topics/myTopic"

    public fun sending(){
        PushNotification(
                NotificationData("Welcome", "dear merchant"),
                TOPIC
        ).also {
            sendNotifiation(it)
        }
    }

    public fun sendNotifiation(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
        try{
            val response =RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d("mysucc", "Response: ${Gson().toJson(response)}")
            }else{
                Log.e("myerror", response.errorBody().toString())
            }
        }catch (e: Exception){
            Log.e("myerror", e.toString())
        }
    }
}