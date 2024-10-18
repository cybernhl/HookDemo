package com.hyb.hookdemo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyb.hookdemo.model.NewsResponse;

public class CounterViewModel extends ViewModel {
    private Counter counter;
    private MutableLiveData<Integer> count;

    private NewsRepository newsRepository;
    private LiveData<NewsResponse> topHeadlines;

    public CounterViewModel() {
        counter = new Counter();
        count = new MutableLiveData<>();
        count.setValue(counter.getCount());
        newsRepository = new NewsRepository();
    }

    public LiveData<Integer> getCount() {
        return count;
    }

    public void increment() {
        counter.increment();
        count.setValue(counter.getCount());
    }

    public void decrement() {
        counter.decrement();
        count.setValue(counter.getCount());
    }

    public void fetchTopHeadlines(String country, String apiKey) {
        topHeadlines = newsRepository.getTopHeadlines(country, apiKey);
    }

    public LiveData<NewsResponse> getTopHeadlines() {
        return topHeadlines;
    }
}
