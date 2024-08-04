package com.ruviapps.androidcalm.feature.model.restApi

import com.ruviapps.androidcalm.feature.model.networkModels.FeatureNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface FeatureRestApis {
  @GET("/api/feature")
  public suspend fun getAllFeature(): Response<List<FeatureNetworkModel>>
}
