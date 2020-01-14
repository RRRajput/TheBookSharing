package com.example.chutiaap.lab_1

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.*


import kotlinx.android.synthetic.main.content_chat_message.*
import kotlinx.android.synthetic.main.single_message.*
import java.sql.Timestamp

////////// from is You ---- To is the other guy
class ChatMessage : AppCompatActivity() {
    var to: String? = null
    var from: String? = null
    var bookID: String? = null
    var fromData: DatabaseReference? = null
    var toData: DatabaseReference? = null
    var sendButton: ImageView? = null
    var TypedMessage: EditText? = null
    var allmessages: ListView? = null
    var username: TextView? = null
    var language: Switch? = null
    var options: Spinner? = null
    var toReturn: Boolean = false
    var isOwner: Boolean = false
    var lang: chat_lang? = null
    var flagView: ImageView? = null
    lateinit var adapter: FirebaseListAdapter<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_chat_message)
        to = intent.getStringExtra("to")
        from = intent.getStringExtra("from")
        bookID = intent.getStringExtra("bookID")
        toReturn = intent.getBooleanExtra("return",false)
        isOwner = intent.getBooleanExtra("owner",false)
        options = findViewById<Spinner>(R.id.options)
        allmessages = findViewById(R.id.Messages)
        allmessages!!.adapter = null
        TypedMessage = findViewById(R.id.TypedMessage)
        username = findViewById(R.id.toID)
        language = findViewById(R.id.switch2)
        flagView = findViewById(R.id.flagView)
        username?.text = to
        TypedMessage!!.setText("")
        sendButton = findViewById(R.id.SendMessageButton)
        fromData = FirebaseDatabase.getInstance().getReference("messages").child(from).child(to)
        toData = FirebaseDatabase.getInstance().getReference("messages").child(to).child(from)

        changeLanguage()

        flagView!!.setOnClickListener {
            changeLanguage()
            adapter.notifyDataSetChanged()
        }
        options!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2==1){
                    if(!bookID.equals("")){
                        FirebaseDatabase.getInstance().getReference("books").child(bookID).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                                //Toast.makeText(applicationContext,"Book Doesn't exist",Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    var book = p0.getValue(Book::class.java)
                                    var mes_obj: Message = Message("",to.toString(),from.toString())
                                    mes_obj.borrow = true
                                    mes_obj.bookid = bookID
                                    mes_obj.bookTitle = book!!.title
                                    mes_obj.owner = to.toString()
                                    fromData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    toData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                }
                            }

                        })
                    }else{
                        var Lib: Intent = Intent(applicationContext,UserLibrary::class.java)
                        Lib.putExtra("user",to.toString())
                        startActivity(Lib)
                        onBackPressed()
                    }
                }
                else if (p2==2){
                        if(toReturn){
                            FirebaseDatabase.getInstance().getReference("books").child(bookID).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError?) {
                                    //Toast.makeText(applicationContext,"Book Doesn't exist",Toast.LENGTH_SHORT).show()
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if(p0.exists()){
                                        var book = p0.getValue(Book::class.java)
                                        var mes_obj: Message = Message("",to.toString(),from.toString())
                                        mes_obj.toreturn = true
                                        mes_obj.bookid = bookID
                                        mes_obj.bookTitle = book!!.title
                                        mes_obj.owner = to.toString()
                                        fromData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                        toData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    }
                                }

                            })
                        }else {
                            var trans: Intent = Intent(applicationContext, CurrentTransactions::class.java)
                            trans.putExtra("them", to.toString())
                            trans.putExtra("you", from.toString())
                            startActivity(trans)
                            onBackPressed()
                        }
                }
                else if (p2==3){
                    FirebaseDatabase.getInstance().getReference("perm_review").child(to.toString()).child(from.toString()).addListenerForSingleValueEvent(object:
                    ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.exists()){
                                if(p0.getValue(Boolean::class.java) as Boolean){
                                    var rev: Intent = Intent(applicationContext,singleReview::class.java)
                                    rev.putExtra("user",to.toString())
                                    startActivity(rev);
                                }
                                else{
                                    Toast.makeText(applicationContext,lang!!.revErr,Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                Toast.makeText(applicationContext,lang!!.revErr,Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }
                else if(p2 == 4){
                    var revPage = Intent(applicationContext,user_rep::class.java)
                    revPage.putExtra("user_id",to.toString())
                    startActivity(revPage)
                }
                options!!.setSelection(0)
            }

        }

        sendButton?.setOnClickListener {
            sendMessage()
        }

        fromData!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                //Toast.makeText(applicationContext,"Error reading messages",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for( ds: DataSnapshot  in p0.children){
                        ds.child("read").ref.setValue(true)
                    }

                }
            }

        })
        adapter = object : FirebaseListAdapter<Message>(
                this,Message::class.java,R.layout.single_message,fromData
        ){
            override fun populateView(v: View, model: Message?, position: Int) {
                var messageFrom = v.findViewById<TextView>(R.id.messageFrom)
                var messageTo = v.findViewById<TextView>(R.id.messageTo)
                var timeFrom = v.findViewById<TextView>(R.id.timefrom)
                var timeTo = v.findViewById<TextView>(R.id.timeto)

                if (model!!.from.equals(from)){
                    messageTo.visibility = View.GONE
                    timeTo.visibility = View.GONE
                    messageFrom.visibility = View.VISIBLE
                    timeFrom.visibility = View.VISIBLE
                    if(model!!.message.equals("")){
                        messageFrom.setBackgroundResource(R.drawable.text_view_chat_red)
                        if (model!!.borrow!!) {
                            if (model!!.owner.equals(to.toString())) {
                                messageFrom.text = lang!!.YouAsk(model!!.bookTitle!!)
                            } else {
                                messageFrom.text = lang!!.YouLend(model!!.bookTitle!!)
                            }
                        }
                        else if (model!!.toreturn!!){
                            if (model!!.owner.equals(to.toString())) {
                                messageFrom.text = lang!!.YouClaim(model!!.bookTitle!!)
                            } else {
                                messageFrom.text = lang!!.YouApprove(model!!.bookTitle!!)
                            }
                        }
                    }
                    else {
                        messageFrom.setBackgroundResource(R.drawable.text_view_chat_from)
                        messageFrom.text = model.message
                    }
                    timeFrom.text =  Timestamp(model.timestamp).toString()
                }
                else{
                    messageFrom.visibility = View.GONE
                    timeFrom.visibility = View.GONE
                    messageTo.visibility = View.VISIBLE
                    timeTo.visibility = View.VISIBLE
                    if(model!!.message.equals("")){
                        messageTo.setBackgroundResource(R.drawable.text_view_chat_blu)
                        if (model!!.borrow!!) {
                            if (model!!.owner.equals(to.toString())) {
                                messageTo.text = lang!!.TheyApprove(model!!.bookTitle!!)
                            } else {
                                messageTo.text = lang!!.TheyAsk(model!!.bookTitle!!)
                            }
                        }
                        else if (model!!.toreturn!!){
                            if (model!!.owner.equals(to.toString())) {
                                messageTo.text = lang!!.TheyApprove(model!!.bookTitle!!)
                            } else {
                                messageTo.text = lang!!.TheyClaim(model!!.bookTitle!!)
                            }
                        }
                    }
                    else {
                        messageTo.setBackgroundResource(R.drawable.text_view_chat_to)
                        messageTo.text = model.message
                    }
                    timeTo.text =  Timestamp(model.timestamp).toString()
                }
            if(allmessages?.adapter != null){
                allmessages!!.setSelection(allmessages!!.adapter.count - 1)
            }
            }

        }

        allmessages?.adapter = adapter

        var namegetter = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val ru: RegisteredUser? = p0?.getValue(RegisteredUser::class.java)
                username?.text = ru?.name
            }

        }
        FirebaseDatabase.getInstance().getReference("users/" + to).addListenerForSingleValueEvent(namegetter)
        allmessages?.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
                var m = p0.adapter.getItem(p2) as Message
                if(m.message.equals("")){
                    if(m.borrow!! and m.owner.equals(from.toString()) and m.to.equals(from.toString())){
                        var ex = Exchange(m.bookid)
                        FirebaseDatabase
                                .getInstance()
                                .getReference("exchanges")
                                .child(from.toString())
                                .child(to.toString())
                                .child(m.bookid)
                                .setValue(ex)
                        FirebaseDatabase.getInstance().getReference("books").child(m.bookid).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                                //Toast.makeText(applicationContext,"book not found",Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    var book = p0.getValue(Book::class.java)
                                    var mes_obj: Message = Message("",to.toString(),from.toString())
                                    mes_obj.borrow = true
                                    mes_obj.bookid = m.bookid
                                    mes_obj.bookTitle = book!!.title
                                    mes_obj.owner = from.toString()
                                    fromData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    toData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    FirebaseDatabase.getInstance().getReference("books").child(m.bookid).child("isAvailable").setValue(false)
                                }
                            }

                        })
                    }
                    else if(m.toreturn!! and m.owner.equals(from.toString()) and m.to.equals(from.toString())){
                        var exDB = FirebaseDatabase
                                .getInstance()
                                .getReference("exchanges")
                                .child(from.toString())
                                .child(to.toString())
                                .child(m.bookid);
                        exDB.child("returned").setValue(true)
                        exDB.child("end").setValue(System.currentTimeMillis())
                        FirebaseDatabase.getInstance().getReference("perm_review").child(from.toString()).child(to.toString()).setValue(true)
                        FirebaseDatabase.getInstance().getReference("perm_review").child(to.toString()).child(from.toString()).setValue(true)
                        Toast.makeText(applicationContext,lang!!.revNOTIF,Toast.LENGTH_SHORT).show()
                        FirebaseDatabase.getInstance().getReference("books").child(m.bookid).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                                //Toast.makeText(applicationContext,"book not found",Toast.LENGTH_SHORT).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    var book = p0.getValue(Book::class.java)
                                    var mes_obj: Message = Message("",to.toString(),from.toString())
                                    mes_obj.toreturn = true
                                    mes_obj.bookid = m.bookid
                                    mes_obj.bookTitle = book!!.title
                                    mes_obj.owner = from.toString()
                                    fromData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    toData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
                                    FirebaseDatabase.getInstance().getReference("books").child(m.bookid).child("isAvailable").setValue(true)
                                }
                            }

                        })
                    }
                }
            }

        })
    }

    private fun spinFiller() {
        var a: ArrayList<String> = ArrayList()
        a.add("")
        a.add(lang!!.borrow)
        a.add(lang!!.ret)
        a.add(lang!!.addreview)
        a.add(lang!!.showreviews)
        options!!.adapter = ArrayAdapter<String>(applicationContext,R.layout.spinner_item,a)
    }

    private fun sendMessage() {
        if (TypedMessage!!.text.toString().isEmpty()){
            Toast.makeText(this,lang!!.errHint,Toast.LENGTH_SHORT).show()
        }
        else {
            val mes: String = TypedMessage!!.text.toString()
            TypedMessage!!.setText("")
            TypedMessage!!.setHint(lang!!.typedMessage)
            var mes_obj: Message = Message(mes,to.toString(),from.toString())
            fromData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)
            toData!!.child(mes_obj.timestamp.toString()).setValue(mes_obj)


        }
    }



    override fun onBackPressed() {
        allmessages!!.adapter = null
        finish()
    }

    fun ToastyMessage(){
        if(toReturn){
            Toast.makeText(applicationContext,lang!!.retToast,Toast.LENGTH_LONG).show()
        }
        else if(!bookID.equals("")){
            Toast.makeText(applicationContext,lang!!.borToast,Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(applicationContext,lang!!.noToast,Toast.LENGTH_LONG).show()
        }
    }

    fun changeLanguage(){
        if(lang == null){
            lang = chat_lang("English")
        }else{
            lang!!.changeLanguage()
        }
        spinFiller()
        TypedMessage!!.setHint(lang!!.typedMessage)
        ToastyMessage()
        if(lang!!.isEng){
            flagView!!.setImageResource(R.drawable.uk)
        }else{
            flagView!!.setImageResource(R.drawable.it)
        }
    }

}
