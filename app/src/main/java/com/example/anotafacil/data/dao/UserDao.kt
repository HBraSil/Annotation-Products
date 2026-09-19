package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anotafacil.data.entity.UserEntity


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}