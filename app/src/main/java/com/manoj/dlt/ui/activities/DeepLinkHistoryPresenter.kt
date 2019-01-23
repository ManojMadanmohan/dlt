package com.manoj.dlt.ui.activities

import android.content.ClipboardManager
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.manoj.dlt.Constants
import com.manoj.dlt.DbConstants
import com.manoj.dlt.DeepLinkTestApplication
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.interfaces.DeepLinkHistoryUpdateListener
import com.manoj.dlt.interfaces.IProfileFeature
import com.manoj.dlt.models.DeepLinkInfo
import com.manoj.dlt.utils.Utilities
import java.util.*
import javax.inject.Inject

/**
 * Created by baldor on 16/6/18.
 */

class DeepLinkHistoryPresenter constructor(var _historyUpdateListener: DeepLinkHistoryUpdateListener, val profileFeature: IProfileFeature){

    private var _previousClipboardText: String? = null
    private var _firebaseListener: ValueEventListener = getFirebaseHistoryListener()


    public fun getInputString(context: Context, currentInput: String): String {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!Utilities.isProperUri(currentInput) && clipboardManager.hasPrimaryClip()) {
            val clipItem = clipboardManager.primaryClip.getItemAt(0)
            if (clipItem != null) {
                if (clipItem.text != null) {
                    val clipBoardText = clipItem.text.toString()
                    if (Utilities.isProperUri(clipBoardText) && clipBoardText != _previousClipboardText) {
                        _previousClipboardText = clipBoardText
                        return clipBoardText;
                    }
                } else if (clipItem.uri != null) {
                    val clipBoardText = clipItem.uri.toString()
                    if (Utilities.isProperUri(clipBoardText) && clipBoardText != _previousClipboardText) {
                        _previousClipboardText = clipBoardText
                        return clipBoardText;
                    }
                }
            }
        }
        return currentInput;
    }

    private fun getFirebaseHistoryListener(): ValueEventListener {
        return object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val deepLinkInfos = ArrayList<DeepLinkInfo>()
                for (child in dataSnapshot.children) {
                    val info = Utilities.getLinkInfo(child)
                    deepLinkInfos.add(info)
                }
                Collections.sort(deepLinkInfos)
                if (_historyUpdateListener != null) {
                    _historyUpdateListener!!.onUpdate(deepLinkInfos)
                }
            }
        }
    }

    public fun attachFirebaseListener(context: Context) {
        if (Constants.isFirebaseAvailable(context)) {
            val baseUserReference = profileFeature.getCurrentUserFirebaseBaseRef()
            val linkReference = baseUserReference.child(DbConstants.USER_HISTORY)
            linkReference.addValueEventListener(_firebaseListener)
        }
    }

    public fun removeFirebaseListener(context: Context) {
        if (Constants.isFirebaseAvailable(context)) {
            val baseUserReference = profileFeature.getCurrentUserFirebaseBaseRef()
            val linkReference = baseUserReference.child(DbConstants.USER_HISTORY)
            linkReference.removeEventListener(_firebaseListener)
        }
    }

}
