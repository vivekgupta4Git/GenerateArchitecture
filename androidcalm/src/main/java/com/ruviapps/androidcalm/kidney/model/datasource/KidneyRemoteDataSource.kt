package com.ruviapps.androidcalm.kidney.model.datasource

import com.ruviapps.androidcalm.kidney.model.domainModels.KidneyModel
import com.ruviapps.androidcalm.kidney.model.restApi.KidneyRestApi
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
