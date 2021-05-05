package hu.bme.ewaste.repository

import android.location.Location
import hu.bme.ewaste.db.FirebaseDb
import java.util.*
import javax.inject.Inject

class TrashCanRepository @Inject
constructor(private val firebaseDb: FirebaseDb) {

    fun writeNewObject(type: String, location: Location, time: Date) {
        firebaseDb.writeNewObject(type, location, time)
    }
}