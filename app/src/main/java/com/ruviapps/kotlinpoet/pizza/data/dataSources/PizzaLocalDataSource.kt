package com.ruviapps.kotlinpoet.pizza.`data`.dataSources

import com.ruviapps.kotlinpoet.pizza.`data`.dao.PizzaDao
import com.ruviapps.kotlinpoet.pizza.`data`.entities.PizzaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class PizzaLocalDataSource(
  private val pizzaDao: PizzaDao,
) {
  public fun getAllPizzaEntity(): Flow<List<PizzaEntity>> =  pizzaDao.getAllPizza()
}
