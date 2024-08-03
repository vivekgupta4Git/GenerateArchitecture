package com.ruviapps.kotlinpoet.kidney.`data`.dataSources

import com.ruviapps.kotlinpoet.kidney.`data`.domainModels.KidneyModel
import com.ruviapps.kotlinpoet.kidney.`data`.restApi.KidneyRestApis
import kotlin.Result

public class KidneyRemoteDataSource(
  private val kidneyRestApis: KidneyRestApis,
) {
  public suspend fun getKidneyModel(): Result<KidneyModel> {
    val result = kidneyRestApis.getKidney()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
