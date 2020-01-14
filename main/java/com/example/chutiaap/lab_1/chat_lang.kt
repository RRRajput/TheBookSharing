package com.example.chutiaap.lab_1

/**
 * Created by Rehan Rajput on 08/06/2018.
 */
class chat_lang(lang: String) {
    init {
        if(lang.equals("English")){
            lang_en();
        }
        else{
            lang_it();
        }
    }

    fun changeLanguage(){
        if(isEng){
            lang_it()
        }
        else{
            lang_en()
        }
    }

    lateinit var typedMessage: String
    lateinit var language: String
    lateinit var borrow: String
    lateinit var ret: String
    lateinit var addreview: String
    lateinit var showreviews: String
    lateinit var retToast: String
    lateinit var borToast: String
    lateinit var noToast: String
    lateinit var revNOTIF: String
    var isEng: Boolean = false

    lateinit var errHint: String
    lateinit var revErr: String

    private fun lang_en() {
        isEng = true
        typedMessage = "Your message.."
        language = "Change language"
        borrow = "Borrow"
        ret = "Return"
        addreview = "Add Review"
        showreviews = "Show Reviews"
        errHint = "Write a message to send.."
        revErr = "You can't leave a review"
        revNOTIF = "You can leave a review for this user now!"
        retToast = "Select the 'Return' option in the menu to return book"
        borToast = "Select the 'Borrow' option to borrow the book"
        noToast = "Check the menù to borrow/return a book"
    }

    private fun lang_it() {
        isEng = false
        typedMessage = "Il tuo messaggio.."
        language = "Cambia lingua"
        borrow = "Richiedi libro"
        ret = "Restituisci"
        addreview = "Scrivi recensione"
        showreviews = "Mostra recensioni"
        errHint = "Scrivi un messaggio prima..."
        revErr = "Non puoi lasciare una recensione"
        revNOTIF = "Ora puoi lasciare una recensione per questo utente!"
        retToast = "Seleziona 'Restituisci' nel menù per restituire il libro "
        borToast = "Seleziona 'Richiedi libro' nel menù per richiedere in prestito il libro"
        noToast = "Controlla il menù per richiedere/restituire un libro"
    }

    fun YouAsk(bookid: String): String{
        if(isEng){
            return "You have requested " + bookid
        }
        return "Hai richiesto il libro " + bookid
    }

    fun YouLend(bookid: String) : String{
        if(isEng){
            return "You lend the book " + bookid
        }
        return "Hai prestato il libro " + bookid
    }

    fun YouClaim(bookid: String) : String{
        if(isEng){
            return "(Waiting for Approval) You claim to have returned the book " + bookid
        }
        return "(in attesa di approvazione) Hai ritornato il libro " + bookid
    }

    fun YouApprove(bookid: String) : String{
        if(isEng){
            return "You approve the return of the book " + bookid
        }
        return "Hai approvato il ritorno del libro " + bookid
    }

    fun TheyAsk(bookid: String): String{
        if(isEng){
            return "The user asks for the book " + bookid + " (Click here to lend the book)"
        }
        return "L'utente ha richiesto il libro " + bookid + " (Clicca qui per approvare)"
    }

    fun TheyLend(bookid: String) : String{
        if(isEng){
            return "The user lended you the book " + bookid
        }
        return "L'utente ha ha prestato il libro " + bookid
    }

    fun TheyClaim(bookid: String) : String{
        if(isEng){
            return "The other user claims to have returned the book " + bookid + " (Click here to approve it)"
        }
        return "L'utente ha ritornato il libro " + bookid + " (Clicca qui per approvare)"
    }

    fun TheyApprove(bookid: String) : String{
        if(isEng){
            return "Your request for the book " + bookid + " has been approved"
        }
        return "La tua richiesta del libro " + bookid + " è stato approvato"
    }
}