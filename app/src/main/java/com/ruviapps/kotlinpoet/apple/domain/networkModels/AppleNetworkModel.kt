package com.ruviapps.kotlinpoet.apple.domain.networkModels

import java.io.Serializable
import kotlin.Int
import kotlin.String

public data class AppleNetworkModel(
  public val id: Int?,
  public val name: String?,
) : Serializable
