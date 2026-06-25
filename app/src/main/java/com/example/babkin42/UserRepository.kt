package com.example.babkin42

class UserRepository(private val userDao: UserDao) {
    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username, password)
    }

    suspend fun register(username: String, email: String, password: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        return if (existingUser == null) {
            userDao.register(UserEntity(username = username, email = email, password = password))
            true
        } else {
            false
        }
    }
}