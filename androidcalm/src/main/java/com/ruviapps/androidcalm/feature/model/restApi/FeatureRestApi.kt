package com.ruviapps.androidcalm.feature.model.restApi

import com.ruviapps.androidcalm.feature.model.domainModels.FeatureModel
import retrofit2.Response
import retrofit2.http.GET

public interface FeatureRestApi {
  @GET("/api/feature")
  public suspend fun getFeature(): Response<FeatureModel>
}
