package com.ruviapps.androidcalm.asthma.model

import retrofit2.Response
import retrofit2.http.GET

public interface AsthmaApi {
  @GET("/api/asthma")
  public suspend fun getAsthma(): Response<ModelClass>
}
