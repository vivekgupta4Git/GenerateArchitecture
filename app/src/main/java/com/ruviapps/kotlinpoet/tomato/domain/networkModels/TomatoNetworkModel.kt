package com.ruviapps.kotlinpoet.tomato.domain.networkModels

import java.io.Serializable
import kotlin.Int
import kotlin.String

public data class TomatoNetworkModel(
  public val id: Int?,
  public val name: String?,
) : Serializable
