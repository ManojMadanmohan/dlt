package com.manoj.dlt.features

import android.content.Context
import com.manoj.dlt.Constants
import com.manoj.dlt.DbConstants
import com.manoj.dlt.events.DeepLinkFireEvent
import com.manoj.dlt.interfaces.IDeepLinkHistory
import com.manoj.dlt.models.DeepLinkInfo
import com.manoj.dlt.models.ResultType
import com.manoj.dlt.utils.SingletonHolder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class DeepLinkHistoryFeature private constructor(contextIn: Context): IDeepLinkHistory{

    val _fileSystem: FileSystem
    val _context: Context = contextIn

    init {
        _fileSystem = FileSystem(_context, Constants.DEEP_LINK_HISTORY_KEY)
        EventBus.getDefault().register(this)
    }

    companion object: SingletonHolder<DeepLinkHistoryFeature, Context> (::DeepLinkHistoryFeature) {

    }

    override fun addLinkToHistory(deepLinkInfo: DeepLinkInfo) {
        if (Constants.isFirebaseAvailable(_context)) {
            addLinkToFirebaseHistory(deepLinkInfo)
        } else {
            addLinkToFileSystemHistory(deepLinkInfo)
        }
    }

    override fun removeLinkFromHistory(deepLinkId: String) {
        if (Constants.isFirebaseAvailable(_context)) {
            removeLinkFromFirebaseHistory(deepLinkId)
        } else {
            removeLinkFromFileSystemHistory(deepLinkId)
        }
    }

    override fun clearAllHistory() {
        if (Constants.isFirebaseAvailable(_context)) {
            clearFirebaseHistory()
        } else {
            clearFileSystemHistory()
        }
    }

    override fun getLinkHistoryFromFileSystem(): ArrayList<DeepLinkInfo> {
        val deepLinks = ArrayList<DeepLinkInfo>()
        for (deepLinkInfoJson in _fileSystem.values()) {
            var deepLinkInfo = DeepLinkInfo.fromJson(deepLinkInfoJson);
            if(deepLinkInfo != null) {
                deepLinks.add(deepLinkInfo)
            }
        }
        Collections.sort(deepLinks)
        return deepLinks
    }

    @Subscribe(sticky = true, priority = 1)
    fun onEvent(deepLinkFireEvent: DeepLinkFireEvent) {
        if (deepLinkFireEvent.resultType == ResultType.SUCCESS) {
            addLinkToHistory(deepLinkFireEvent.deepLinkInfo!!)
        }
    }

    private fun addLinkToFileSystemHistory(deepLinkInfo: DeepLinkInfo) {
        _fileSystem.write(deepLinkInfo.id, DeepLinkInfo.toJson(deepLinkInfo))
    }

    private fun addLinkToFirebaseHistory(deepLinkInfo: DeepLinkInfo) {
        val baseUserReference = ProfileFeature.getInstance(_context).getCurrentUserFirebaseBaseRef()
        val linkReference = baseUserReference.child(DbConstants.USER_HISTORY).child(deepLinkInfo.id)
        val infoMap = object : HashMap<String, Any?>() {
            init {
                put(DbConstants.DL_ACTIVITY_LABEL, deepLinkInfo.activityLabel)
                put(DbConstants.DL_DEEP_LINK, deepLinkInfo.deepLink)
                put(DbConstants.DL_PACKAGE_NAME, deepLinkInfo.packageName)
                put(DbConstants.DL_UPDATED_TIME, deepLinkInfo.updatedTime)
            }
        }
        linkReference.setValue(infoMap)
    }

    private fun clearFileSystemHistory() {
        _fileSystem.clearAll()
    }

    private fun clearFirebaseHistory() {
        val baseUserReference = ProfileFeature.getInstance(_context).getCurrentUserFirebaseBaseRef()
        val historyRef = baseUserReference.child(DbConstants.USER_HISTORY)
        historyRef.setValue(null)
    }

    private fun removeLinkFromFileSystemHistory(deepLinkId: String) {
        _fileSystem.clear(deepLinkId)
    }

    private fun removeLinkFromFirebaseHistory(deepLinkId: String) {
        val baseUserReference = ProfileFeature.getInstance(_context).getCurrentUserFirebaseBaseRef()
        val linkReference = baseUserReference.child(DbConstants.USER_HISTORY).child(deepLinkId)
        linkReference.setValue(null)
    }

    private fun migrateHistoryToFirebase() {
        if (Constants.isFirebaseAvailable(_context)) {
            for (info in getLinkHistoryFromFileSystem()) {
                addLinkToHistory(info)
            }
            clearFileSystemHistory()
        }
    }

}