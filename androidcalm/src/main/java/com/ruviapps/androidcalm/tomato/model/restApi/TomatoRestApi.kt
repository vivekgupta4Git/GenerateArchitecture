package com.ruviapps.androidcalm.tomato.model.restApi

import com.ruviapps.androidcalm.tomato.model.networkModels.TomatoNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface TomatoRestApi {
  @GET("/api/tomato")
  public suspend fun getAllTomato(): Response<List<TomatoNetworkModel>>
}
