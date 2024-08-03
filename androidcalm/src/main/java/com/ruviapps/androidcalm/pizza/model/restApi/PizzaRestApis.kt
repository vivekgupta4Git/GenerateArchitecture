package com.ruviapps.androidcalm.pizza.model.restApi

import com.ruviapps.androidcalm.pizza.model.domainModels.PizzaModel
import retrofit2.Response
import retrofit2.http.GET

public interface PizzaRestApis {
  @GET("/api/pizza")
  public suspend fun getPizza(): Response<PizzaModel>
}
