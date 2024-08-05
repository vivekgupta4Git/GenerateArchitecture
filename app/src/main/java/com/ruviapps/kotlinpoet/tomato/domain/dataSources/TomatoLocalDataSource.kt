package com.ruviapps.kotlinpoet.tomato.domain.dataSources

import com.ruviapps.kotlinpoet.tomato.domain.dao.TomatoDao
import com.ruviapps.kotlinpoet.tomato.domain.entities.TomatoEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class TomatoLocalDataSource(
  private val tomatoDao: TomatoDao,
) {
  public fun getAllTomatoEntity(): Flow<List<TomatoEntity>> =  tomatoDao.getAllTomato()
}
