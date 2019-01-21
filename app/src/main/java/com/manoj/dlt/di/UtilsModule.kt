package com.manoj.dlt.di

import android.content.Context
import com.manoj.dlt.Constants
import com.manoj.dlt.features.FileSystem
import com.manoj.dlt.interfaces.IFileSystem
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module(includes = [ContextModule::class])
class UtilsModule {

    @Provides
    @Named(Constants.GLOBAL_PREF_KEY)
    fun getGlobalPrefStore(context: Context): IFileSystem {
        return FileSystem(context, Constants.GLOBAL_PREF_KEY)
    }


    @Provides
    @Named(Constants.DEEP_LINK_HISTORY_KEY)
    fun getHistoryPrefStore(context: Context): IFileSystem {
        return FileSystem(context, Constants.DEEP_LINK_HISTORY_KEY)
    }

}