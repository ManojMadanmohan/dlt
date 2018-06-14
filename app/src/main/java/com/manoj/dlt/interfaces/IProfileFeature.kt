package com.manoj.dlt.interfaces

import com.google.firebase.database.DatabaseReference

interface IProfileFeature {
    abstract fun getUserId(): String
    abstract fun getCurrentUserFirebaseBaseRef(): DatabaseReference
}