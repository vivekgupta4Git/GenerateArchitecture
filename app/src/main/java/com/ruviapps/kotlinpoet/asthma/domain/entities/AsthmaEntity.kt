package com.ruviapps.kotlinpoet.asthma.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int
import kotlin.String

@Entity
public data class AsthmaEntity(
  @PrimaryKey
  public val id: Int,
  public val name: String?,
)
