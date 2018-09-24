package com.manoj.dlt

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Constants{
    enum class CONFIG {
        SANDBOX, PRODUCTION
    }

     const val DEEP_LINK_HISTORY_KEY = "deep_link_history_key_v1"
     const val GLOBAL_PREF_KEY = "one_time_key"
     const val APP_TUTORIAL_SEEN = "app_tut_seen"
     const val SHORTCUT_HINT_SEEN = "shortcut_hint_seen"
     const val USER_ID_KEY = "user_id"
     const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=com.manoj.dlt"
     const val WEB_APP_LINK = "https://sweltering-fire-2158.firebaseapp.com/"
     const val PRIVACY_POLICY_URL = "https://manojmadanmohan.github.io/dlt/privacy_policy"
     var ENVIRONMENT:CONFIG = BuildConfig.CONFIG

    @JvmStatic
    fun getFirebaseUserRef() : DatabaseReference
    {
        return FirebaseDatabase.getInstance()
                .getReference(ENVIRONMENT.name.toLowerCase())
                .child(DbConstants.USERS);
    }

    @JvmStatic
    fun isFirebaseAvailable(context: Context) : Boolean
    {
        val playServicesAvl = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        return if (playServicesAvl == ConnectionResult.SUCCESS) {
            true
        } else {
            false
        } 
    }
}