package com.ruviapps.kotlinpoet.apple.domain.dataSources

import com.ruviapps.kotlinpoet.apple.domain.dao.AppleDao
import com.ruviapps.kotlinpoet.apple.domain.entities.AppleEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class AppleLocalDataSource(
  private val appleDao: AppleDao,
) {
  public fun getAllAppleEntity(): Flow<List<AppleEntity>> =  appleDao.getAllApple()
}
