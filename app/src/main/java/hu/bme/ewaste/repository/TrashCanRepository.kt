package hu.bme.ewaste.repository

import hu.bme.ewaste.data.dto.DetectionDTO
import hu.bme.ewaste.data.dto.DetectionResponse
import hu.bme.ewaste.data.dto.Location
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

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

    suspend fun getNearDetectedTrashCans(location: Location): List<DetectionResponse> {
        return try {
             client.get("/nearDetections"){
                parameter("lat", location.lat)
                parameter("lng", location.lng)
            }
        } catch (e: Exception) {
            // TODO
            return emptyList()
        }
    }

    suspend fun emptyTrashCan(trashCanId: UUID) {
        try {
            client.delete("emptyTrashCan/$trashCanId")
        } catch (e: Exception) {
            // TODO
        }
    }
}