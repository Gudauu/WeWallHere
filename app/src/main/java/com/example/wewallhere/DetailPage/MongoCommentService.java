package com.example.wewallhere.DetailPage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MongoCommentService{
    @GET("mongometa/{type}")
    Call<List<MongoCommentEntry>> getCommentMetaList(
            @Path("type") String type
    );
}
