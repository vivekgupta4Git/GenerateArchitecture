package com.ruviapps.kotlinpoet.kidney.`data`.restApi

import com.ruviapps.kotlinpoet.kidney.`data`.networkModels.KidneyNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface KidneyRestApis {
  @GET("/api/kidney")
  public suspend fun getAllKidney(): Response<List<KidneyNetworkModel>>
}
