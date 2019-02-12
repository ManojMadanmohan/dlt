package com.manoj.dlt

import android.app.Activity
import android.app.Application
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.FirebaseDatabase
import com.manoj.dlt.di.AppComponent
import com.manoj.dlt.di.ContextModule
import com.manoj.dlt.di.DaggerAppComponent
import com.manoj.dlt.features.DeepLinkHistoryFeature
import com.manoj.dlt.features.LinkQueueHandler
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.utils.Utilities
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerApplication
import dagger.internal.DaggerCollections
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class DeepLinkTestApplication: DaggerApplication() {

    @Inject
    lateinit var _profileFeature: ProfileFeature
    @Inject
    lateinit var _linkQueueHandler: LinkQueueHandler

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }

    override fun onCreate() {
        if(Constants.isFirebaseAvailable(this)) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
        super.onCreate()

        if(Constants.ENVIRONMENT.equals(Constants.CONFIG.PRODUCTION)) {
            Fabric.with(this, Crashlytics())
            Crashlytics.setUserIdentifier(_profileFeature.getUserId())
            Crashlytics.setString("user id", _profileFeature.getUserId())
        } else
        {
            Toast.makeText(applicationContext, "In Testing mode", Toast.LENGTH_LONG).show()
        }
        if(Constants.isFirebaseAvailable(this))
        {
            _linkQueueHandler.runQueueListener()
        }
        Utilities.initializeAppRateDialog(applicationContext)
    }
}
