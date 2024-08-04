package com.ruviapps.androidcalm.feature.model.dataSources

import com.ruviapps.androidcalm.feature.model.networkModels.FeatureNetworkModel
import com.ruviapps.androidcalm.feature.model.restApi.FeatureRestApis
import kotlin.Result
import kotlin.collections.List

public class FeatureRemoteDataSource(
  private val featureRestApis: FeatureRestApis,
) {
  public suspend fun getAllFeatureNetworkModel(): Result<List<FeatureNetworkModel>> {
    val result = featureRestApis.getAllFeature()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
