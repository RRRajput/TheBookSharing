package com.example.chutiaap.lab_1

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class AllChats : AppCompatActivity() {

    var chatview: ListView? = null
    var chatlist: ArrayList<RegisteredUser>? = null
    var messagesdb: DatabaseReference? = null
    var usersdb: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chats)
        chatview = findViewById(R.id.chat_list)

        chatlist = ArrayList()
        messagesdb = FirebaseDatabase.getInstance().getReference("messages").child(FirebaseAuth.getInstance().uid.toString())
        usersdb = FirebaseDatabase.getInstance().getReference("users")
        var mescount: Int = 0
        var usercount: Int = 0
        var mesbool: Boolean = false

        var UserListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,"Cancelled",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {

                if(p0!!.exists()){
                    var ru: RegisteredUser? = p0!!.getValue(RegisteredUser::class.java)
                    chatlist?.add(ru!!)
                    usercount++
                }
                if(mesbool && mescount.equals(usercount)){
                    var adapt: ChatAdapter = ChatAdapter(applicationContext,chatlist!!)
                    chatview!!.adapter = adapt
                    chatview!!.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                        var ru: RegisteredUser = adapterView.adapter.getItem(i) as RegisteredUser
                        var chatIntent: Intent = Intent(applicationContext,ChatMessage::class.java)
                        chatIntent.putExtra("from",FirebaseAuth.getInstance().uid)
                        chatIntent.putExtra("to",ru.userid)
                        chatIntent.putExtra("bookID","")
                        startActivity(chatIntent)
                    }
                }
            }

        }
        var messageListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,"Cancelled",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.exists()){

                    mesbool = false
                    mescount=0
                    for(p: DataSnapshot in p0.children){
                        var key: String = p.key
                        mescount++
                        usersdb?.child(key)!!.addListenerForSingleValueEvent(UserListener)
                        if(p0.childrenCount.equals(mescount.toLong())){
                            mesbool = true
                        }
                    }
                }
                else {
                    Toast.makeText(applicationContext,"You have no chats",Toast.LENGTH_LONG).show()
                }
            }

        }
        messagesdb?.addValueEventListener(messageListener)


    }

    override fun onBackPressed() {
        var main: Intent = Intent(this,MainActivity::class.java)
        startActivity(main)
        finish();
    }

    class ChatAdapter(context: Context,chatitems: ArrayList<RegisteredUser>): BaseAdapter(){
        var context: Context? = null
        var chatitems: ArrayList<RegisteredUser>? = null
        init {
            this.context = context
            this.chatitems = chatitems
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var v: View = LayoutInflater.from(this.context).inflate(R.layout.all_chat_item,null)
            var name: TextView = v.findViewById(R.id.ChatName)
            var pic: ImageView = v.findViewById(R.id.chatImage)
            var User: RegisteredUser? = this.chatitems?.get(p0)
            name.text = User?.name + " " + User?.surname

            if(User?.name.equals("") && User?.surname.equals("")){
                name.setText("(No Name)")
                name.setTypeface(name.typeface,Typeface.ITALIC)
            }
            
            try{
                FirebaseStorage.getInstance().getReference("images/" + User!!.userid + ".jpeg")
                        .downloadUrl.addOnSuccessListener { taskSnapshot ->
                    Glide.with(this.context).load(taskSnapshot).error(R.drawable.user_id2).into(pic)
                }.addOnFailureListener { ErrException ->
                    pic.setImageResource(R.drawable.user_id2)
                }
            }
            catch (e: Exception){
                e.printStackTrace();
            }
            return v
        }

        override fun getItem(p0: Int): Any {
            return this!!.chatitems!![p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return this.chatitems!!.size

        }

    }
}
