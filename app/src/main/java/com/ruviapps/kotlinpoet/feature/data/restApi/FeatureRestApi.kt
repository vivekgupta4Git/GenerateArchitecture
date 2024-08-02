package com.ruviapps.kotlinpoet.feature.`data`.restApi

import com.ruviapps.kotlinpoet.feature.`data`.domainModels.FeatureModel
import retrofit2.Response
import retrofit2.http.GET

public interface FeatureRestApi {
  @GET("/api/feature")
  public suspend fun getFeature(): Response<FeatureModel>
}
