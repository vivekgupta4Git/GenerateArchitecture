package com.ruviapps.kotlinpoet.tomato.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.ruviapps.kotlinpoet.tomato.domain.entities.TomatoEntity
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

@Dao
public interface TomatoDao {
    @Query("SELECT * FROM TomatoEntity")
    public fun getAllTomato(): Flow<List<TomatoEntity>>
}
