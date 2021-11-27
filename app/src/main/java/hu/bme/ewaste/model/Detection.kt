package hu.bme.ewaste.model

import java.util.*


data class Detection(
    var localId: UUID,
    var type: TrashCanType,
    var location: Location
)

data class Location(
    var lat: Double,
    var long: Double
)
