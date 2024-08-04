package com.ruviapps.androidcalm.asthma.model.dataSources

import com.ruviapps.androidcalm.asthma.model.networkModels.AsthmaNetworkModel
import com.ruviapps.androidcalm.asthma.model.restApi.AsthmaRestApis
import kotlin.Result
import kotlin.collections.List

public class AsthmaRemoteDataSource(
  private val asthmaRestApis: AsthmaRestApis,
) {
  public suspend fun getAllAsthmaNetworkModel(): Result<List<AsthmaNetworkModel>> {
    val result = asthmaRestApis.getAllAsthma()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
