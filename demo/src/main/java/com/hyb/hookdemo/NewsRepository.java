package com.hyb.hookdemo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyb.hookdemo.model.NewsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private NewsApiService newsApiService;

    public NewsRepository() {
        newsApiService = RetrofitClient.getNewsApiService();
    }

    public LiveData<NewsResponse> getTopHeadlines(String country, String apiKey) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();

        newsApiService.getTopHeadlines(country, apiKey).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}
