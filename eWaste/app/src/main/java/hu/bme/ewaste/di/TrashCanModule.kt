package hu.bme.ewaste.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.ewaste.service.TrashCanTracker
import hu.bme.ewaste.util.TrashCanObjectDetector
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrashCanModule {

    @Provides
    @Singleton
    fun provideTrashCanObjectDetector(): TrashCanObjectDetector {
        return TrashCanObjectDetector()
    }

    @Provides
    @Singleton
    fun provideTrashCanTracker(): TrashCanTracker {
        return TrashCanTracker()
    }
}