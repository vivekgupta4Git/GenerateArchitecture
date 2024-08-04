package com.ruviapps.androidcalm.asthma.model.dataSources

import com.ruviapps.androidcalm.asthma.model.dao.AsthmaDao
import com.ruviapps.androidcalm.asthma.model.entities.AsthmaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class AsthmaLocalDataSource(
  private val asthmaDao: AsthmaDao,
) {
  public fun getAllAsthmaEntity(): Flow<List<AsthmaEntity>> =  asthmaDao.getAllAsthma()
}
