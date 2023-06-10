package com.example.wewallhere.DetailPage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MongoCommentService{
    @GET("mongometa/comment/{ID_reply}")
    Call<List<MongoCommentEntry>> getCommentMetaList(
            @Path("ID_reply") String ID_reply
    );
    @GET("mongometa/comment/history/{email}")
    Call<List<MongoCommentEntry>> getHistoryCommentMetaList(
            @Path("email") String email
    );

}
