package hu.bme.ewaste.repository

import hu.bme.ewaste.data.dto.DetectionDTO
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TrashCanRepository(
    private val client: HttpClient
) {

    suspend fun sendDetectedTrashCans(detectionDTOs: List<DetectionDTO>) {
        println("new detections: $detectionDTOs")
        try {
            client.post<String>("/detections"){
                contentType(ContentType.Application.Json)
                body = detectionDTOs
            }
        } catch (e: Exception) {
            // TODO
        }
    }
}