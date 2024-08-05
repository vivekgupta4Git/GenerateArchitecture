package com.ruviapps.androidcalm.tomato.model.dataSources

import com.ruviapps.androidcalm.tomato.model.dao.TomatoDao
import com.ruviapps.androidcalm.tomato.model.entities.TomatoEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class TomatoLocalDataSource(
  private val tomatoDao: TomatoDao,
) {
  public fun getAllTomatoEntity(): Flow<List<TomatoEntity>> =  tomatoDao.getAllTomato()
}
