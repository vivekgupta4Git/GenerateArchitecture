package com.ruviapps.kotlinpoet.feature.`data`.networkModels

import java.io.Serializable
import kotlin.Int
import kotlin.String

public data class FeatureNetworkModel(
  public val id: Int?,
  public val name: String?,
) : Serializable
