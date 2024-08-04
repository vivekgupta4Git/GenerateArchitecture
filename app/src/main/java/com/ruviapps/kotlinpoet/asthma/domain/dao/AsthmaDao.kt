package com.ruviapps.kotlinpoet.asthma.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.asthma.domain.entities.AsthmaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface AsthmaDao {
  @Query("SELECT * FROM AsthmaEntity")
  public fun getAllAsthma(): Flow<List<AsthmaEntity>>
}
