package com.example.scoutanddine.data.repositories

import androidx.annotation.WorkerThread
import com.example.scoutanddine.data.daos.UserProfileDao
import com.example.scoutanddine.data.entities.UserProfile
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    val userProfiles: Flow<List<UserProfile>> = userProfileDao.getProfiles()

    @WorkerThread
    suspend fun insert(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    @WorkerThread
    suspend fun update(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    @WorkerThread
    suspend fun delete(userProfile: UserProfile) {
        userProfileDao.delete(userProfile)
    }
}