package com.ruviapps.kotlinpoet.pizza.`data`.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int
import kotlin.String

@Entity
public data class PizzaEntity(
  @PrimaryKey
  public val id: Int,
  public val name: String,
)
