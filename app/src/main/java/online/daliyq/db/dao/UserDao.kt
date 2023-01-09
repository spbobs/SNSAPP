package online.daliyq.db.dao

import androidx.room.*
import online.daliyq.db.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(vararg users: UserEntity)

    @Update
    suspend fun update(vararg users: UserEntity)

    @Delete
    suspend fun delete(vararg userEntity: UserEntity)

    @Query("SELECT * FROM user WHERE id=:uid")
    suspend fun get(uid: String): UserEntity?
}