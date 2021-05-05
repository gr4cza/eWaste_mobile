package hu.bme.ewaste.repository

import hu.bme.ewaste.db.FirebaseDb
import javax.inject.Inject

class TrashCanRepository @Inject
constructor(private val firebaseDb: FirebaseDb) {

    fun writeNewObject() {
        firebaseDb.writeNewObject()
    }
}