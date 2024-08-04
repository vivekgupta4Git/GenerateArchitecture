package com.ruviapps.kotlinpoet.feature.`data`.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.feature.`data`.entities.FeatureEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface FeatureDao {
  @Query("SELECT * FROM FeatureEntity")
  public fun getAllFeature(): Flow<List<FeatureEntity>>
}
