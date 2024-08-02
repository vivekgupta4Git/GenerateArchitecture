package com.ruviapps.kotlinpoet.asthma.data

import retrofit2.Response
import retrofit2.http.GET

public interface AsthmaApi {
  @GET("/api/asthma")
  public suspend fun getAsthma(): Response<DataClass>
}
