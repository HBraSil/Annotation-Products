package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.anotafacil.data.entity.UserEntity


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserDao(user: UserEntity)

    @Query("UPDATE user SET ownerId = :ownerId WHERE uid = :uid")
    suspend fun updateOwnerId(uid: String, ownerId: String?)

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserDao(uid: String): UserEntity?

    @Query("DELETE FROM user")
    suspend fun deleteUserDao()
}