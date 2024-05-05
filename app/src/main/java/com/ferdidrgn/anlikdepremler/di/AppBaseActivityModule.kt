package com.ferdidrgn.anlikdepremler.di

import android.app.Activity
import com.ferdidrgn.anlikdepremler.util.base.BaseActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object AppBaseActivityModule {

    @Singleton
    @Provides
    fun provideBaseActivity(activity: Activity): BaseActivity<*, *> {
        check(activity is BaseActivity<*, *>) { "Every Activity is expected to extend BaseActivity" }
        return activity
    }
}