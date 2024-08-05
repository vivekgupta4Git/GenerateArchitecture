package com.ruviapps.androidcalm.apple.model.dataSources

import com.ruviapps.androidcalm.apple.model.dao.AppleDao
import com.ruviapps.androidcalm.apple.model.entities.AppleEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class AppleLocalDataSource(
  private val appleDao: AppleDao,
) {
  public fun getAllAppleEntity(): Flow<List<AppleEntity>> =  appleDao.getAllApple()
}
