package hu.bme.ewaste.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import hu.bme.ewaste.util.TrashCanObjectDetector
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object TrashCanModule {


}