package com.example.esportstracker;



import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/Register")
    /*Call<ResponseBody> createUser(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("alias") String alias
    );*/
    Call<Void> registerUser(@Body User user);
    //Call<Void> registerUser(@Body String user);

    @POST("api/Login")
    Call<Void> loginUser(@Body User user);

    @POST("api/LoginAdmin")
    Call<Void> loginAdmin(@Body User user);

    @POST("api/Event")
    Call<Void> setEvent(@Body evento evento);

    @GET("api/Event")
    Call<JsonElement> getEvent();

    @GET("api/EventFiltered")
    Call<JsonElement> getEventFiltered(@Query("searchTerm") String searchTerm);

    @POST("api/EventTracking")
    Call<Void> SetEvenTracking(@Body JsonObject Ids);

    @GET("api/EventTracking")
    Call<JsonElement> GetEventTracking(@Query("idUser") String idUser);
}
