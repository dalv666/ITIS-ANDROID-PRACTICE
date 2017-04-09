package com.googlemaps.template.myapplication.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.googlemaps.template.myapplication.util.RequestResult;
import com.googlemaps.template.myapplication.response.Response;

import java.io.IOException;

public abstract class BaseLoader extends AsyncTaskLoader<Response> {

    public BaseLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Response loadInBackground() {
        try {
            Response response = apiCall();
            if (response.getRequestResult() == RequestResult.SUCCESS) {
                response.save(getContext());
                onSuccess();
            } else {
                onError();
            }
            return response;
        } catch (IOException e) {
            onError();
            return null;
        }
    }

    protected void onSuccess() {
    }

    protected void onError() {
    }

    protected abstract Response apiCall() throws IOException;
}