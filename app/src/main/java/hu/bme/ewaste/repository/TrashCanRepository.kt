package hu.bme.ewaste.repository

import hu.bme.ewaste.model.Detection

class TrashCanRepository {

    fun writeNewObject(detection: Detection) {
        println("new detection: $detection")
    }
}