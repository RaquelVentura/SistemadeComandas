package com.example.sistemadecomandas.vistasCocineros.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CocinerosSlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CocinerosSlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}