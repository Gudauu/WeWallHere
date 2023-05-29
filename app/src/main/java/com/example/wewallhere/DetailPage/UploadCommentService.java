package com.example.wewallhere.DetailPage;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadCommentService {
    @Multipart
    @POST("/uploadComment/media") // Replace with your server's upload endpoint
    Call<ResponseBody> uploadImage(
            @Part("ID") RequestBody ID,
            @Part("ID_reply") RequestBody ID_reply,
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content
    );

    @Multipart
    @POST("/uploadComment/media") // Replace with your server's upload endpoint
    Call<ResponseBody> uploadVideo(
            @Part("ID") RequestBody ID,
            @Part("ID_reply") RequestBody ID_reply,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content
    );

    @Multipart
    @POST("/uploadComment/text") // Replace with your server's upload endpoint
    Call<ResponseBody> uploadText(
            @Part("ID") RequestBody ID,
            @Part("ID_reply") RequestBody ID_reply,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content
    );
}

