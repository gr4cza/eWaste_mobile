package hu.bme.ewaste.data.dto

import hu.bme.ewaste.data.model.TrashCanType
import hu.bme.ewaste.util.TrashCanTypeSerializer
import hu.bme.ewaste.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class DetectionDTO(
    @Serializable(with = UUIDSerializer::class)
    var localId: UUID,
    var type: TrashCanType,
    var location: Location
)

@Serializable
data class DetectionResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = TrashCanTypeSerializer::class)
    val type: TrashCanType,
    val location: Location
)

@Serializable
data class Location(
    var lat: Double,
    var lng: Double
)
