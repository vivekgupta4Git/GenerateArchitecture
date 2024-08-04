package com.ruviapps.kotlinpoet.asthma.domain.networkModels

import java.io.Serializable
import kotlin.Int
import kotlin.String

public data class AsthmaNetworkModel(
  public val id: Int?,
  public val name: String?,
) : Serializable
