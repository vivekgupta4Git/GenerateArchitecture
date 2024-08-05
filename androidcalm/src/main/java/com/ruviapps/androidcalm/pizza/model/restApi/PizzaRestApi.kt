package com.ruviapps.androidcalm.pizza.model.restApi

import com.ruviapps.androidcalm.pizza.model.networkModels.PizzaNetworkModel
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.GET

public interface PizzaRestApi {
  @GET("/api/pizza")
  public suspend fun getAllPizza(): Response<List<PizzaNetworkModel>>
}
