package com.ruviapps.androidcalm.pizza.model.dataSources

import com.ruviapps.androidcalm.pizza.model.networkModels.PizzaNetworkModel
import com.ruviapps.androidcalm.pizza.model.restApi.PizzaRestApis
import kotlin.Result

public class PizzaRemoteDataSource(
  private val pizzaRestApis: PizzaRestApis,
) {
  public suspend fun getAllPizzaNetworkModel(): Result<PizzaNetworkModel> {
    val result = pizzaRestApis.getAllPizza()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
