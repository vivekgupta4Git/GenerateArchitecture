package com.ruviapps.kotlinpoet.kidney.`data`.dataSources

import com.ruviapps.kotlinpoet.kidney.`data`.dao.KidneyDao
import com.ruviapps.kotlinpoet.kidney.`data`.entities.KidneyEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class KidneyLocalDataSource(
  private val kidneyDao: KidneyDao,
) {
  public fun getAllKidneyEntity(): Flow<List<KidneyEntity>> =  kidneyDao.getAllKidney()
}
