package com.example.sistemadecomandas.vistasAdmin.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminSlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdminSlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}