package com.ruviapps.kotlinpoet.pizza.`data`.dataSources

import com.ruviapps.kotlinpoet.pizza.`data`.networkModels.PizzaNetworkModel
import com.ruviapps.kotlinpoet.pizza.`data`.restApi.PizzaRestApis
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
