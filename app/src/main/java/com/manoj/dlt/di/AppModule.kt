package com.manoj.dlt.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule constructor(val context: Context) {

    @Provides
    fun getAppContext(): Context {
        return context.applicationContext
    }
}