package com.ruviapps.kotlinpoet.kidney.`data`.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int
import kotlin.String

@Entity
public data class KidneyEntity(
  @PrimaryKey
  public val id: Int,
  public val name: String,
)
