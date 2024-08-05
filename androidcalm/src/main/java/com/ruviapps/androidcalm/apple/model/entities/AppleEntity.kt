package com.ruviapps.androidcalm.apple.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int
import kotlin.String

@Entity
public data class AppleEntity(
  @PrimaryKey
  public val id: Int,
  public val name: String?,
)
