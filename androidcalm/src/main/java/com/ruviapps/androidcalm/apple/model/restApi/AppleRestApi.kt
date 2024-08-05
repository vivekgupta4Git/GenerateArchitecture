package com.ruviapps.androidcalm.apple.model.restApi

import com.ruviapps.androidcalm.apple.model.networkModels.AppleNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface AppleRestApi {
  @GET("/api/apple")
  public suspend fun getAllApple(): Response<List<AppleNetworkModel>>
}
