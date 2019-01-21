package com.manoj.dlt.di

import com.manoj.dlt.features.DeepLinkHistoryFeature
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.interfaces.IDeepLinkHistory
import com.manoj.dlt.interfaces.IProfileFeature
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
abstract class FeaturesModule {

    @Singleton
    @Binds
    abstract fun getHistoryFeature(feature: DeepLinkHistoryFeature): IDeepLinkHistory

    @Singleton
    @Binds
    abstract fun getProfileFeature(feature: ProfileFeature): IProfileFeature
}