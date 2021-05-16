package hu.bme.ewaste.db

import android.location.Location
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hu.bme.ewaste.model.TrashCanData
import timber.log.Timber
import java.util.*

class FirebaseDb {

    private val database = Firebase.database.getReference("trash_cans")

    fun writeNewObject(trackingSessionID: UUID, type: String, location: Location, time: Date) {
        database.child(trackingSessionID.toString())
            .child(time.toString())
            .setValue(
                TrashCanData(
                    type = type,
                    lat = location.latitude,
                    long = location.longitude
                )
            )
            .addOnCanceledListener {
                Timber.d("Error on adding $trackingSessionID, $type Location: $location")
            }
    }

}