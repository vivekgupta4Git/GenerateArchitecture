package com.ruviapps.androidcalm.asthma.model.restApi

import com.ruviapps.androidcalm.asthma.model.networkModels.AsthmaNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface AsthmaRestApis {
  @GET("/api/asthma")
  public suspend fun getAllAsthma(): Response<List<AsthmaNetworkModel>>
}
