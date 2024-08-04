package com.ruviapps.androidcalm.feature.model.dataSources

import com.ruviapps.androidcalm.feature.model.dao.FeatureDao
import com.ruviapps.androidcalm.feature.model.entities.FeatureEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class FeatureLocalDataSource(
  private val featureDao: FeatureDao,
) {
  public fun getAllFeatureEntity(): Flow<List<FeatureEntity>> =  featureDao.getAllFeature()
}
