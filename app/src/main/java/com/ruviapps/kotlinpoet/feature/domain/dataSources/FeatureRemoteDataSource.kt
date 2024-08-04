package com.ruviapps.kotlinpoet.feature.domain.dataSources

import com.ruviapps.kotlinpoet.feature.domain.networkModels.FeatureNetworkModel
import com.ruviapps.kotlinpoet.feature.domain.restApi.FeatureRestApis
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
