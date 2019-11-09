package com.sable.businesslistingapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jaink on 14-09-2017.
 */



public interface RetrofitArrayApi {

    @GET("wp-json/geodir/v2/business")
    Call<List<BusinessListings>> getPostInfo();

    @GET("wp-json/geodir/v2/business/?post_type=gd_business")
    Call<List<BusinessListings>> getSearch(
            @Query("search") String query);

    @GET("wp-json/wc/v3/products?consumer_key=ck_c3addab1f230fa55025a2f78969d18f518ebbc5e&consumer_secret=cs_aaf9c39669e92ebd745a0e91a9a5810e9222cc92")
    Call<List<WooProducts>> getPostWooInfo();

    @GET("wp-json/geodir/v2/business/categories")
    Call<List<ListingsCategories>> getCategory();

/// to make call to dynamic URL
 // @GET
  //Call<List<BusinessListings>> getPostInfo(@Url String url);
//
    @FormUrlEncoded
    @POST("wp-json/geodir/v2/business/{id}")
    Call<List<BusinessListings>> postData(
          @Path("id") String id,
          @Field("title") String title,
          @Field("street") String street,
          @Field("city") String city,
          @Field("state") String state,
          @Field("zip") String zip,
          @Field("phone") String phone,
          @Field("latitude") String latitude,
          @Field("longitude") String longitude,
          @Field("bldgno") String bldgno,
          //@Field("email") String email,
          //@Field("website") String website,
          @Field("content") String content,
          @Field("hours") String hours);
 }

