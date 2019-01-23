package com.manoj.dlt.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.manoj.dlt.DeepLinkTestApplication
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.interfaces.IDeepLinkHistory
import com.manoj.dlt.interfaces.IProfileFeature
import com.manoj.dlt.ui.activities.DeepLinkHistoryActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FeaturesModule::class,
    AndroidInjectionModule::class, AndroidSupportInjectionModule::class,
    ActivityModule::class])
public interface AppComponent: AndroidInjector<DeepLinkTestApplication> {

    fun getContext(): Context

    fun getProfileFeature(): IProfileFeature

    fun getDeepLinkHistoryFeature(): IDeepLinkHistory

//    @Component.Builder
//    interface Builder {
//
//        @BindsInstance
//        fun application(application: Application): Builder
//
//        fun build(): AppComponent
//    }
}