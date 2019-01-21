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

class DeepLinkTestApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    companion object {
        lateinit var component: AppComponent
        private set
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    fun getComponent(): AppComponent = component

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
        component.inject(this)
        if(Constants.ENVIRONMENT.equals(Constants.CONFIG.PRODUCTION)) {
            Fabric.with(this, Crashlytics())
            Crashlytics.setUserIdentifier(component.getProfileFeature().getUserId())
            Crashlytics.setString("user id", component.getProfileFeature().getUserId())
        } else
        {
            Toast.makeText(applicationContext, "In Testing mode", Toast.LENGTH_LONG).show()
        }
        if(Constants.isFirebaseAvailable(this))
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            LinkQueueHandler.getInstance(this).runQueueListener()
        }
        Utilities.initializeAppRateDialog(applicationContext)
    }
}
