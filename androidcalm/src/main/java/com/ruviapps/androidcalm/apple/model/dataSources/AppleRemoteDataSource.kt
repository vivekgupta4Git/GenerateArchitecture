package com.ruviapps.androidcalm.apple.model.dataSources

import com.ruviapps.androidcalm.apple.model.networkModels.AppleNetworkModel
import com.ruviapps.androidcalm.apple.model.restApi.AppleRestApi
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
