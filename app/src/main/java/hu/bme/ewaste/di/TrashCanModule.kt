package hu.bme.ewaste.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.bme.ewaste.detector.TrashCanObjectDetector
import hu.bme.ewaste.repository.TrashCanRepository
import hu.bme.ewaste.service.TrashCanTracker
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrashCanModule {

    @Provides
    @Singleton
    fun provideTrashCanObjectDetector(@ApplicationContext appContext: Context): TrashCanObjectDetector {
        return TrashCanObjectDetector(appContext)
    }

    @Provides
    @Singleton
    fun provideTrashCanTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        trashCanRepository: TrashCanRepository,
        @ApplicationContext appContext: Context
    ): TrashCanTracker = TrashCanTracker(fusedLocationProviderClient, trashCanRepository, appContext)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }

    @Provides
    @Singleton
    fun provideTrashCanRepository(httpClient: HttpClient) = TrashCanRepository(httpClient)

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android){
            install(Logging){
                level = LogLevel.ALL
            }

            install(JsonFeature){
                serializer = KotlinxSerializer()
            }
        }
    }
}