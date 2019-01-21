package com.manoj.dlt.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule constructor(val context: Context) {

    @Provides
    @Singleton
    fun getAppContext(): Context {
        return context.applicationContext
    }
}