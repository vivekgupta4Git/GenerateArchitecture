package com.ruviapps.kotlinpoet.kidney.`data`.dataSources

import com.ruviapps.kotlinpoet.kidney.`data`.networkModels.KidneyNetworkModel
import com.ruviapps.kotlinpoet.kidney.`data`.restApi.KidneyRestApis
import kotlin.Result
import kotlin.collections.List

public class KidneyRemoteDataSource(
  private val kidneyRestApis: KidneyRestApis,
) {
  public suspend fun getAllKidneyNetworkModel(): Result<List<KidneyNetworkModel>> {
    val result = kidneyRestApis.getAllKidney()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
