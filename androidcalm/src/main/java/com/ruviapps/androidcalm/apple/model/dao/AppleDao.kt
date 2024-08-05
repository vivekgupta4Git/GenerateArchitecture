package com.ruviapps.androidcalm.apple.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.apple.model.entities.AppleEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface AppleDao {
  @Query("SELECT * FROM AppleEntity")
  public fun getAllApple(): Flow<List<AppleEntity>>
}
