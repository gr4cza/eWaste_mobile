package hu.bme.ewaste.db

import android.location.Location
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hu.bme.ewaste.model.TrashCanData
import java.util.*

class FirebaseDb {

    private val database = Firebase.database.getReference("trash_cans")

    fun writeNewObject(type: String, location: Location, time: Date) {
        database.child(time.time.toString()).setValue(TrashCanData(
            type = type,
            lat = location.latitude,
            long = location.longitude
        ))
    }

}