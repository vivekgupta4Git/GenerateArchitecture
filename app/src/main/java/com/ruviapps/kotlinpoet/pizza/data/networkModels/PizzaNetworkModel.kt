package com.ruviapps.kotlinpoet.pizza.`data`.networkModels

import java.io.Serializable
import kotlin.Int
import kotlin.String

public data class PizzaNetworkModel(
  public val id: Int?,
  public val name: String?,
) : Serializable
