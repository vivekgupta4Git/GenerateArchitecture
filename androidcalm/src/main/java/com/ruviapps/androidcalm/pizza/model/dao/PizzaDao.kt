package com.ruviapps.androidcalm.pizza.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.androidcalm.pizza.model.entities.PizzaEntity
import kotlin.collections.List
import kotlinx.coroutines.flow.Flow

@Dao
public interface PizzaDao {
  @Query("SELECT * FROM PizzaEntity")
  public fun getAllPizza(): Flow<List<PizzaEntity>>
}
