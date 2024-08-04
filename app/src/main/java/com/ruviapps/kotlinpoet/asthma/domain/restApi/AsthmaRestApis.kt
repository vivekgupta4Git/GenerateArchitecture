package com.ruviapps.kotlinpoet.asthma.domain.restApi

import com.ruviapps.kotlinpoet.asthma.domain.networkModels.AsthmaNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface AsthmaRestApis {
  @GET("/api/asthma")
  public suspend fun getAllAsthma(): Response<List<AsthmaNetworkModel>>
}
