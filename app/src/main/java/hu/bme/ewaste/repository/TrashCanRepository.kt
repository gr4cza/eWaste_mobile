package hu.bme.ewaste.repository

import hu.bme.ewaste.model.Detection
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TrashCanRepository(
    private val client: HttpClient
) {

    suspend fun writeNewObject(detection: Detection) {
        println("new detection: $detection")
        try {
            client.post<String>("/detections"){
                contentType(ContentType.Application.Json)
                body = listOf(detection)
            }
        } catch (e: Exception) {
        }
    }
}