package com.ruviapps.kotlinpoet.feature.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.feature.domain.entities.FeatureEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface FeatureDao {
  @Query("SELECT * FROM FeatureEntity")
  public fun getAllFeature(): Flow<List<FeatureEntity>>
}
