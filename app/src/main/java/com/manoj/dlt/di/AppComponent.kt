package com.manoj.dlt.di

import android.app.Application
import android.content.Context
import com.manoj.dlt.DeepLinkTestApplication
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.interfaces.IDeepLinkHistory
import com.manoj.dlt.interfaces.IProfileFeature
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FeaturesModule::class, AndroidInjectionModule::class])
public interface AppComponent{

    fun inject(application: DeepLinkTestApplication)

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