package com.example.chutiaap.lab_1.services


import android.app.*

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.example.chutiaap.lab_1.ChatMessage
import com.example.chutiaap.lab_1.R.drawable.message_icon
import com.example.chutiaap.lab_1.RegisteredUser

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


/**
 * Created by Rehan Rajput on 20/05/2018.
 */
class service() : IntentService("com.example.chutiaap.lab_1.services.service") {


    val channel_id = "BNotify"
    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(p0: Intent?) {
        val from: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages").child(FirebaseAuth.getInstance().uid.toString())
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val users: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        var msgs: ArrayList<NotifMsg> = ArrayList()
        @RequiresApi(Build.VERSION_CODES.O)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channel_id,"notifier",NotificationManager.IMPORTANCE_MAX)

            //
            notificationChannel.description = "channel for notification"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED

            notificationManager.createNotificationChannel(notificationChannel)
        }

        from.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                //Toast.makeText(applicationContext,"Database Error",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var curr_users: Int = 0
                    var tot_users: Int = p0.childrenCount.toInt()
                    for(MainDS: DataSnapshot in p0.children){
                        var name = MainDS.key
                        var i: Int = 0
                        for (ds: DataSnapshot  in MainDS.children){
                            var a: Boolean = ds.child("read").getValue() as Boolean
                            if(!a && ds.child("to").getValue(String::class.java).equals(FirebaseAuth.getInstance().uid)){
                                i++
                            }
                        }
                        users.child(name).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                                //Toast.makeText(applicationContext,"Database Error",Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                var ru = p0.getValue(RegisteredUser::class.java) as RegisteredUser
                                name = ru.name
                                if(i != 0){
                                    msgs.add(NotifMsg(name,i,ru.userid))
                                }
                                curr_users++
                                Log.d("ServiceTag",curr_users.toLong().toString() + ":" + tot_users.toString())
                                if(tot_users.equals(curr_users))
                                    sendNotification(msgs,notificationManager)
                            }

                        })

                    }
                }
            }

        })

        Log.d("ServiceTag","Service Ended")
    }

    fun sendNotification(nots: ArrayList<NotifMsg>,notificationManager: NotificationManager) {
        var resid: Int = packageManager
                .getResourcesForApplication("com.example.chutiaap.lab_1")
                .getIdentifier("message_icon","drawable","com.example.chutiaap.lab_1")
        for (msg: NotifMsg in nots)
        {
            var title = msg.getTitle()
            var text = msg.getText()
            var notificationIntent: Intent = Intent(applicationContext,ChatMessage::class.java)
            notificationIntent.putExtra("from",FirebaseAuth.getInstance().uid.toString())
            notificationIntent.putExtra("to",msg.id)

            var contentIntent: PendingIntent = PendingIntent.getActivity(applicationContext,0,notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT)

            val notificationbuilder = NotificationCompat.Builder(this, channel_id)
            notificationbuilder
                    .setContentIntent(contentIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(resid)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis())

            notificationManager.notify(msg.id.hashCode(), notificationbuilder.build())

        }
    }

    class NotifMsg(name:String,msgs: Int,id: String) {
        var name = name
        var msgs = msgs
        var id = id

        fun IncMsg(){
            this.msgs++
        }

        fun getTitle() : String{
            return name
        }

        fun getText(): String{
            return msgs.toString() + " new messages"
        }
    }
}