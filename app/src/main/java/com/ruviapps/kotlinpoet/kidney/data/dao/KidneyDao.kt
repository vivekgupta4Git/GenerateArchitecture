package com.ruviapps.kotlinpoet.kidney.`data`.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.kidney.`data`.entities.KidneyEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface KidneyDao {
  @Query("SELECT * FROM KidneyEntity")
  public fun getAllKidney(): Flow<List<KidneyEntity>>
}
