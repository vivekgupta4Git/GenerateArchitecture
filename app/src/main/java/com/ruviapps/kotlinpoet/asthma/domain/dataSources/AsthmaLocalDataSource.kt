package com.ruviapps.kotlinpoet.asthma.domain.dataSources

import com.ruviapps.kotlinpoet.asthma.domain.dao.AsthmaDao
import com.ruviapps.kotlinpoet.asthma.domain.entities.AsthmaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class AsthmaLocalDataSource(
  private val asthmaDao: AsthmaDao,
) {
  public fun getAllAsthmaEntity(): Flow<List<AsthmaEntity>> =  asthmaDao.getAllAsthma()
}
