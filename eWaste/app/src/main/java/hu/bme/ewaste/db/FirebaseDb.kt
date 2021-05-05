package hu.bme.ewaste.db

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDb {

    private val database = Firebase.database.getReference("trash_cans")

    fun writeNewObject() {
        database.setValue("test")
    }

}