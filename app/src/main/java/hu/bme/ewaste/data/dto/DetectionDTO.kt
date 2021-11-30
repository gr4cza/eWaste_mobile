package hu.bme.ewaste.data.dto

import hu.bme.ewaste.data.model.TrashCanType
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
data class Location(
    var lat: Double,
    var long: Double
)
