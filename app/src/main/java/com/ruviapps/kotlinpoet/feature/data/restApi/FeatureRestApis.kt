package com.ruviapps.kotlinpoet.feature.`data`.restApi

import com.ruviapps.kotlinpoet.feature.`data`.networkModels.FeatureNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface FeatureRestApis {
  @GET("/api/feature")
  public suspend fun getAllFeature(): Response<List<FeatureNetworkModel>>
}
