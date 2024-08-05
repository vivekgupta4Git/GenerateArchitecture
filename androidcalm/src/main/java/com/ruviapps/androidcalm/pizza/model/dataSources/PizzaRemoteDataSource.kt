package com.ruviapps.androidcalm.pizza.model.dataSources

import com.ruviapps.androidcalm.pizza.model.networkModels.PizzaNetworkModel
import com.ruviapps.androidcalm.pizza.model.restApi.PizzaRestApi
import kotlin.Result
import kotlin.collections.List

public class PizzaRemoteDataSource(
  private val pizzaRestApi: PizzaRestApi,
) {
  public suspend fun getAllPizzaNetworkModel(): Result<List<PizzaNetworkModel>> {
    val result = pizzaRestApi.getAllPizza()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
