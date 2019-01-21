package com.manoj.dlt.di

import android.app.Application
import com.manoj.dlt.DeepLinkTestApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
public interface AppComponent{

    fun inject(application: DeepLinkTestApplication)

//    @Component.Builder
//    interface Builder {
//
//        @BindsInstance
//        fun application(application: Application): Builder
//
//        fun build(): AppComponent
//    }
}