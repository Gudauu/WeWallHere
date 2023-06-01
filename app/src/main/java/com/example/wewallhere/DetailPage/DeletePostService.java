package com.example.wewallhere.DetailPage;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DeletePostService {
    @Headers("Content-Type: application/json")
    @POST("/delete/") // Replace with your server's upload endpoint
    Call<Void> deletePost(@Body RequestBody body);

}


