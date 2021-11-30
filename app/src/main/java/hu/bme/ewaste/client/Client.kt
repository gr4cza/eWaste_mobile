package hu.bme.ewaste.client

import hu.bme.ewaste.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

fun createClient(): HttpClient {
    return HttpClient(Android) {
        defaultRequest {
            host = BuildConfig.BASE_URL
            url {
                protocol =
                    (if (BuildConfig.FLAVOR == "local") URLProtocol.HTTP else URLProtocol.HTTPS)
            }
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
}