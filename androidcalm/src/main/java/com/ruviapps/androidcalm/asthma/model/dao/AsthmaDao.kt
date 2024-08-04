package com.ruviapps.androidcalm.asthma.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.asthma.model.entities.AsthmaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface AsthmaDao {
  @Query("SELECT * FROM AsthmaEntity")
  public fun getAllAsthma(): Flow<List<AsthmaEntity>>
}
