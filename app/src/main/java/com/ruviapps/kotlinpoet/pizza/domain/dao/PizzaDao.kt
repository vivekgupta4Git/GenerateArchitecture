package com.ruviapps.kotlinpoet.pizza.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.pizza.domain.entities.PizzaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface PizzaDao {
  @Query("SELECT * FROM PizzaEntity")
  public fun getAllPizza(): Flow<List<PizzaEntity>>
}
