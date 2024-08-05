package com.ruviapps.kotlinpoet.tomato.domain.dataSources

import com.ruviapps.kotlinpoet.tomato.domain.networkModels.TomatoNetworkModel
import com.ruviapps.kotlinpoet.tomato.domain.restApi.TomatoRestApi
import kotlin.Result
import kotlin.collections.List

public class TomatoRemoteDataSource(
  private val tomatoRestApi: TomatoRestApi,
) {
  public suspend fun getAllTomatoNetworkModel(): Result<List<TomatoNetworkModel>> {
    val result = tomatoRestApi.getAllTomato()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
