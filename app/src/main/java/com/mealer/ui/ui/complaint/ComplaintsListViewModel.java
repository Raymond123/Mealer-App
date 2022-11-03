package com.mealer.ui.ui.complaint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComplaintsListViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ComplaintsListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is complaints list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
