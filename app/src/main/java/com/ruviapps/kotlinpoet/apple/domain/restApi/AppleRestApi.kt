package com.ruviapps.kotlinpoet.apple.domain.restApi

import com.ruviapps.kotlinpoet.apple.domain.networkModels.AppleNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface AppleRestApi {
  @GET("/api/apple")
  public suspend fun getAllApple(): Response<List<AppleNetworkModel>>
}
