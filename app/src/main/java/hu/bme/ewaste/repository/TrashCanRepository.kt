package hu.bme.ewaste.repository

import hu.bme.ewaste.model.Detection
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*

class TrashCanRepository(
    private val client: HttpClient
) {

    suspend fun writeNewObject(detection: Detection) {
        println("new detection: $detection")
        try {
            client.post<MultiPartData.Empty>("http://192.168.1.79:8080/detections"){
                contentType(ContentType.Application.Json)
                body = listOf(detection)
            }
        } catch (e: Exception) {
        }
    }
}