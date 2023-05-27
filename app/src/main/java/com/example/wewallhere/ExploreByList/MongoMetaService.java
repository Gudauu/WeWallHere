package com.example.wewallhere.ExploreByList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MongoMetaService {
    @GET("mongometa/{type}")
    Call<List<MongoMediaEntry>> getMetaDataList(
            @Path("type") String type
    );
}

//public interface MediaService {
//    @GET("media/{type}/{id}")
//    Call<List<MediaEntry>> getMediaList(
//            @Path("type") String type,
//            @Path("id") String id
//    );
//}
