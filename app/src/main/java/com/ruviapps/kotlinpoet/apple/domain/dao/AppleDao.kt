package com.ruviapps.kotlinpoet.apple.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.apple.domain.entities.AppleEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface AppleDao {
  @Query("SELECT * FROM AppleEntity")
  public fun getAllApple(): Flow<List<AppleEntity>>
}
