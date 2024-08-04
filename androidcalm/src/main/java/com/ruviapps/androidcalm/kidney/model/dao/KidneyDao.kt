package com.ruviapps.androidcalm.kidney.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.kidney.model.entities.KidneyEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface KidneyDao {
  @Query("SELECT * FROM KidneyEntity")
  public fun getAllKidney(): Flow<List<KidneyEntity>>
}
