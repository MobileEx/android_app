package com.pilates.app.service.listener;

import com.android.volley.VolleyError;

public interface OnRequestResult<T> {
    void requestComplete(T data);
    void requestError(VolleyError ex);
}
