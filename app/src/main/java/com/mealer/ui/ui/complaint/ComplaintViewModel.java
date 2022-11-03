package com.mealer.ui.ui.complaint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComplaintViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ComplaintViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is complaint fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}