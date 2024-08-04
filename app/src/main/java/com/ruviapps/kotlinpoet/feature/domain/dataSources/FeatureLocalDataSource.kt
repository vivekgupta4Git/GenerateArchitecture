package com.ruviapps.kotlinpoet.feature.domain.dataSources

import com.ruviapps.kotlinpoet.feature.domain.dao.FeatureDao
import com.ruviapps.kotlinpoet.feature.domain.entities.FeatureEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class FeatureLocalDataSource(
  private val featureDao: FeatureDao,
) {
  public fun getAllFeatureEntity(): Flow<List<FeatureEntity>> =  featureDao.getAllFeature()
}
