package com.manoj.dlt.features

import android.content.Context
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.manoj.dlt.Constants
import com.manoj.dlt.interfaces.IProfileFeature
import com.manoj.dlt.utils.SingletonHolder
import com.manoj.dlt.utils.Utilities
import java.util.*

class ProfileFeature private constructor(context: Context): IProfileFeature
{
    private val _fileSystem: FileSystem
    private var _userId: String?

    init {
        _fileSystem = Utilities.getOneTimeStore(context)
        _userId = _fileSystem.read(Constants.USER_ID_KEY)
        if(_userId == null)
        {
            _userId = generateUserId()
            _fileSystem.write(Constants.USER_ID_KEY, _userId!!)
        }

        Log.d("profile", "user id = " + _userId)
    }

    companion object: SingletonHolder<ProfileFeature, Context>(::ProfileFeature) {

    }

    override fun getUserId(): String {
        return _userId!!
    }

    override fun getCurrentUserFirebaseBaseRef(): DatabaseReference {
        val baseUserRef = Constants.getFirebaseUserRef()
        return baseUserRef.child(_userId)
    }

    private fun generateUserId(): String {
        //TODO: better implementation
        val rand = UUID.randomUUID().toString()
        return rand.substring(0, 5)
    }



}
