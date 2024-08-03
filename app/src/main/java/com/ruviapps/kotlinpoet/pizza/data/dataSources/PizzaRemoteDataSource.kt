package com.ruviapps.kotlinpoet.pizza.`data`.dataSources

import com.ruviapps.kotlinpoet.pizza.`data`.domainModels.PizzaModel
import com.ruviapps.kotlinpoet.pizza.`data`.restApi.PizzaRestApis
import kotlin.Result

public class PizzaRemoteDataSource(
  private val pizzaRestApis: PizzaRestApis,
) {
  public suspend fun getPizzaModel(): Result<PizzaModel> {
    val result = pizzaRestApis.getPizza()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
