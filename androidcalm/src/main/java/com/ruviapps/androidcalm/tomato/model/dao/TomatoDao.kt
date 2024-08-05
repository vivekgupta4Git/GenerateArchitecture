package com.ruviapps.androidcalm.tomato.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.tomato.model.entities.TomatoEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface TomatoDao {
  @Query("SELECT * FROM TomatoEntity")
  public fun getAllTomato(): Flow<List<TomatoEntity>>
}
