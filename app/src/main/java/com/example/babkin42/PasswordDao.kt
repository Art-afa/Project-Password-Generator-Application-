package com.example.babkin42

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords WHERE username = :username ORDER BY id DESC")
    fun getPasswordsForUser(username: String): Flow<List<PasswordEntity>>

    @Insert
    suspend fun insertPassword(password: PasswordEntity)

    @Delete
    suspend fun deletePassword(password: PasswordEntity)
}