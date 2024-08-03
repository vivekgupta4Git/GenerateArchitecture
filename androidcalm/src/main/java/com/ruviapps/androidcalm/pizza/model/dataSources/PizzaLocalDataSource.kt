package com.ruviapps.androidcalm.pizza.model.dataSources

import com.ruviapps.androidcalm.pizza.model.dao.PizzaDao
import com.ruviapps.androidcalm.pizza.model.entities.PizzaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

public class PizzaLocalDataSource(
  private val pizzaDao: PizzaDao,
) {
  public fun getAllPizzaEntity(): Flow<List<PizzaEntity>> =  pizzaDao.getAllPizza()
}
