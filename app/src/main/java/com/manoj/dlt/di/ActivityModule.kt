package com.manoj.dlt.di

import com.manoj.dlt.ui.activities.DeepLinkHistoryActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun annotateAndroidInjectionMappingForDeepLinkHistoryActivity(): DeepLinkHistoryActivity
}