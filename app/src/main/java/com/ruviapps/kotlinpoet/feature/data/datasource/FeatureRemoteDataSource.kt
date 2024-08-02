package com.ruviapps.kotlinpoet.feature.`data`.datasource

import com.ruviapps.kotlinpoet.feature.`data`.domainModels.FeatureModel
import com.ruviapps.kotlinpoet.feature.`data`.restApi.FeatureRestApi
import kotlin.Result

public class FeatureRemoteDataSource(
  private val featureRestApi: FeatureRestApi,
) {
  public suspend fun getFeatureModel(): Result<FeatureModel> {
    val result = featureRestApi.getFeature()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
