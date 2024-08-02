package com.ruviapps.androidcalm.feature.model.datasource

import com.ruviapps.androidcalm.feature.model.domainModels.FeatureModel
import com.ruviapps.androidcalm.feature.model.restApi.FeatureRestApi
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
