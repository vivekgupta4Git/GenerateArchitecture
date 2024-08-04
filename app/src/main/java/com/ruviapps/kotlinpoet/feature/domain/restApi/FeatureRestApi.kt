package com.ruviapps.kotlinpoet.feature.domain.restApi

import com.ruviapps.kotlinpoet.feature.domain.networkModels.FeatureNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface FeatureRestApi {
  @GET("/api/feature")
  public suspend fun getAllFeature(): Response<List<FeatureNetworkModel>>
}
