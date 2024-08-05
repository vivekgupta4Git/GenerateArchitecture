package com.ruviapps.kotlinpoet.apple.domain.dataSources

import com.ruviapps.kotlinpoet.apple.domain.networkModels.AppleNetworkModel
import com.ruviapps.kotlinpoet.apple.domain.restApi.AppleRestApi
import kotlin.Result
import kotlin.collections.List

public class AppleRemoteDataSource(
  private val appleRestApi: AppleRestApi,
) {
  public suspend fun getAllAppleNetworkModel(): Result<List<AppleNetworkModel>> {
    val result = appleRestApi.getAllApple()
    return if(result.isSuccessful && result.body() != null) {
      Result.success(result.body()!!)
    } else {
      Result.failure(Throwable("Unable to fetch"))
    }
  }
}
