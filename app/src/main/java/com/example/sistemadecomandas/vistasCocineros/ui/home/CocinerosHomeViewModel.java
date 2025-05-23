package com.example.sistemadecomandas.vistasCocineros.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CocinerosHomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CocinerosHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}