package com.ruviapps.kotlinpoet.feature.domain.restApi

import com.ruviapps.kotlinpoet.feature.domain.networkModels.FeatureNetworkModel
import kotlin.String
import kotlin.collections.List
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

public interface FeatureRestApi {
  @GET("/api/feature")
  public suspend fun getAllFeature(): Response<List<FeatureNetworkModel>>

  @POST("api/feature")
  public suspend fun insertFeature(@Body featureRequest: FeatureNetworkModel):
      Response<FeatureNetworkModel>

  @PUT("api/feature/{id}")
  public suspend fun updateFeature(@Path("id") id: String, @Body
      featureRequest: FeatureNetworkModel): Response<FeatureNetworkModel>

  @DELETE("api/feature/{id}")
  public suspend fun deleteFeature(@Path("id") id: String): Response<FeatureNetworkModel>

  @GET("/api/feature/{id}")
  public suspend fun getFeatureById(@Path("id") id: String): Response<FeatureNetworkModel>
}
