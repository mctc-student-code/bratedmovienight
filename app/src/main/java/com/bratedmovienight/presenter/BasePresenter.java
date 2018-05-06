package com.bratedmovienight.presenter;

import com.bratedmovienight.view.BaseView;

public interface BasePresenter<V extends BaseView> {
    void start();
    void stop();
}
