package com.ruviapps.androidcalm.kidney.model.dataSources

import com.ruviapps.androidcalm.kidney.model.dao.KidneyDao
import com.ruviapps.androidcalm.kidney.model.entities.KidneyEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class KidneyLocalDataSource(
  private val kidneyDao: KidneyDao,
) {
  public fun getAllKidneyEntity(): Flow<List<KidneyEntity>> =  kidneyDao.getAllKidney()
}
