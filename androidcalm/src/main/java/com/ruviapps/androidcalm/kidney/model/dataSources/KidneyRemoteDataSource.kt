package com.ruviapps.androidcalm.kidney.model.dataSources

import com.ruviapps.androidcalm.kidney.model.domainModels.KidneyModel
import com.ruviapps.androidcalm.kidney.model.restApi.KidneyRestApis
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
