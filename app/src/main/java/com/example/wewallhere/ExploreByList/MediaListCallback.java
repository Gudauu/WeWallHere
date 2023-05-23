package com.example.wewallhere.ExploreByList;

import java.util.List;

public interface MediaListCallback {
    void onMediaListLoaded(List<MediaEntry> mediaList);
    void onMediaListFailed(String errorMessage);
}
