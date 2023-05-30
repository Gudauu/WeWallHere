package com.example.wewallhere.User;

import com.example.wewallhere.ExploreByList.MongoMediaEntry;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface UserInfoService {
    @Multipart
    @POST("/uploadUser")
        // Replace with your server's upload endpoint
    Call<ResponseBody> uploadInfo(
            @Part MultipartBody.Part image,
            @Part("username") RequestBody username,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email
    );

    @GET("mongometa/userinfo/{email}")
    Call<UserInfo> getUserInfo(
            @Path("email") String email
    );
}
