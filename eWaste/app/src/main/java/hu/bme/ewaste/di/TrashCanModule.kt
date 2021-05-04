package hu.bme.ewaste.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    fun provideTrashCanObjectDetector(): TrashCanObjectDetector = TrashCanObjectDetector()

    @Provides
    @Singleton
    fun provideTrashCanTracker(fusedLocationProviderClient: FusedLocationProviderClient): TrashCanTracker =
        TrashCanTracker(fusedLocationProviderClient)


    @Provides
    @Singleton
    fun fusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }
}