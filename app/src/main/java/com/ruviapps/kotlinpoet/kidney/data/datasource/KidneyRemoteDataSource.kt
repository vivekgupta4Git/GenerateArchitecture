package com.ruviapps.kotlinpoet.kidney.`data`.datasource

import com.ruviapps.kotlinpoet.kidney.`data`.domainModels.KidneyModel
import com.ruviapps.kotlinpoet.kidney.`data`.restApi.KidneyRestApi
import kotlin.Result

public class KidneyRemoteDataSource(
  private val kidneyRestApi: KidneyRestApi,
) {
  public suspend fun getKidneyModel(): Result<KidneyModel> {
    val result = kidneyRestApi.getKidney()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
