package com.ruviapps.androidcalm.tomato.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int
import kotlin.String

@Entity
public data class TomatoEntity(
  @PrimaryKey
  public val id: Int,
  public val name: String?,
)
