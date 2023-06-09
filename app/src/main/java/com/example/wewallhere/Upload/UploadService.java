package com.example.wewallhere.Upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {
    @Multipart
    @POST("/upload") // Replace with your server's upload endpoint
    Call<ResponseBody> uploadImage(
            @Part("ID") RequestBody ID,
            @Part MultipartBody.Part image,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email
    );

    @Multipart
    @POST("/upload") // Replace with your server's upload endpoint
    Call<ResponseBody> uploadVideo(
            @Part("ID") RequestBody ID,
            @Part MultipartBody.Part video,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email
    );
}


