package com.ruviapps.kotlinpoet.pizza.domain.dataSources

import com.ruviapps.kotlinpoet.pizza.domain.dao.PizzaDao
import com.ruviapps.kotlinpoet.pizza.domain.entities.PizzaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class PizzaLocalDataSource(
  private val pizzaDao: PizzaDao,
) {
  public fun getAllPizzaEntity(): Flow<List<PizzaEntity>> =  pizzaDao.getAllPizza()
}
