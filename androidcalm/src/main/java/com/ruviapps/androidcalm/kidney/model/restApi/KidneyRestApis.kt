package com.ruviapps.androidcalm.kidney.model.restApi

import com.ruviapps.androidcalm.kidney.model.domainModels.KidneyModel
import retrofit2.Response
import retrofit2.http.GET

public interface KidneyRestApis {
  @GET("/api/kidney")
  public suspend fun getKidney(): Response<KidneyModel>
}
