package com.ruviapps.kotlinpoet.kidney.`data`.restApi

import com.ruviapps.kotlinpoet.kidney.`data`.domainModels.KidneyModel
import retrofit2.Response
import retrofit2.http.GET

public interface KidneyRestApi {
  @GET("/api/kidney")
  public suspend fun getKidney(): Response<KidneyModel>
}
