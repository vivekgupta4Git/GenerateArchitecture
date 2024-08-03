package com.ruviapps.kotlinpoet.pizza.`data`.restApi

import com.ruviapps.kotlinpoet.pizza.`data`.domainModels.PizzaModel
import retrofit2.Response
import retrofit2.http.GET

public interface PizzaRestApis {
  @GET("/api/pizza")
  public suspend fun getPizza(): Response<PizzaModel>
}
