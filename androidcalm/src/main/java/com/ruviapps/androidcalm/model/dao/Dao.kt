package com.ruviapps.androidcalm.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.model.entities.Entity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface Dao {
  @Query("SELECT * FROM Entity")
  public fun getAll(): Flow<List<Entity>>
}
