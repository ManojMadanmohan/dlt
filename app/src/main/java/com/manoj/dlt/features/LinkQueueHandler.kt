package com.manoj.dlt.features

import android.content.Context
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.manoj.dlt.Constants
import com.manoj.dlt.DbConstants
import com.manoj.dlt.utils.FirebaseChildAddedListener
import com.manoj.dlt.utils.SingletonHolder
import com.manoj.dlt.utils.Utilities

class LinkQueueHandler private constructor(context: Context) {

    private var _isProcessing: Boolean = false
    private val context: Context = context
    private val _queueListener: ChildEventListener
    private val _queueReference: DatabaseReference

    init {
        _queueReference = ProfileFeature.getInstance(context).getCurrentUserFirebaseBaseRef().child(DbConstants.LINK_QUEUE)
        _queueListener = getQueueListener()
    }

    companion object : SingletonHolder<LinkQueueHandler, Context>(::LinkQueueHandler){

    }

    fun runQueueListener() {
        if (_isProcessing) {
            //Already attached listener on queue. do nothing
            return
        } else if (Constants.isFirebaseAvailable(context)) {
            _queueReference.addChildEventListener(_queueListener)
            _isProcessing = true
        }
    }

    fun stopQueueListener() {
        if (_isProcessing) {
            _queueReference.removeEventListener(_queueListener)
            _isProcessing = false
        }
    }

    private fun getQueueListener(): FirebaseChildAddedListener {
        return object : FirebaseChildAddedListener() {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
                val qId = dataSnapshot.key
                val deepLink = dataSnapshot.value.toString()
                Utilities.checkAndFireDeepLink(deepLink, context)
                Utilities.logLinkViaWeb(deepLink, ProfileFeature.getInstance(context).getUserId(), context)
                _queueReference.child(qId).setValue(null)
            }
        }
    }

}