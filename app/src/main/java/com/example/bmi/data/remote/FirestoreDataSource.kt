package com.example.bmi.data.remote

import com.example.bmi.domain.model.Profile
import com.example.bmi.domain.model.User
import com.example.bmi.domain.model.WeightEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createProfile(profile: Profile) {
        firestore.collection("accounts")
            .document(profile.accountId)
            .collection("profiles")
            .document(profile.id)
            .set(profile)
            .await()
    }

    suspend fun getProfiles(accountId: String): List<Profile> {
        val snapshot = firestore.collection("accounts")
            .document(accountId)
            .collection("profiles")
            .get()
            .await()

        return snapshot.toObjects(Profile::class.java)
    }

    suspend fun getProfile(
        accountId: String,
        profileId: String
    ): Profile? {
        val snapshot = firestore.collection("accounts")
            .document(accountId)
            .collection("profiles")
            .document(profileId)
            .get()
            .await()

        return snapshot.toObject(Profile::class.java)
    }

    suspend fun updateProfile(profile: Profile) {
        firestore.collection("accounts")
            .document(profile.accountId)
            .collection("profiles")
            .document(profile.id)
            .set(profile)
            .await()
    }

    suspend fun saveUser(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun getUser(userId: String): User? {
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
    }

    suspend fun addWeightEntry(entry: WeightEntry) {
        firestore.collection("accounts")
            .document(entry.accountId)
            .collection("profiles")
            .document(entry.profileId)
            .collection("weight_history")
            .add(entry)
            .await()
    }

    suspend fun getWeightHistory(
        accountId: String,
        profileId: String
    ): List<WeightEntry> {

        val snapshot = firestore.collection("accounts")
            .document(accountId)
            .collection("profiles")
            .document(profileId)
            .collection("weight_history")
            .orderBy("timestamp")
            .get()
            .await()

        return snapshot.toObjects(WeightEntry::class.java)
    }
}