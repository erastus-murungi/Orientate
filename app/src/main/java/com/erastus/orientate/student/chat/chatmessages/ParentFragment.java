package com.erastus.orientate.student.chat.chatmessages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.erastus.orientate.utils.circularimageview.ParentActivityImpl;

import org.jetbrains.annotations.NotNull;

public abstract class ParentFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    protected Context fragmentContext;
    private boolean mIsFromCache;
    private View rootView;

    protected ParentActivityImpl hostActivity;

    public abstract int provideLayoutResourceId();

    public abstract void performBindings(View rootView);

    public abstract void setViewBehaviour(boolean viewFromCache);

    public abstract void onReady();

    public void extractArguments() {
    }

    public View getRootView() {
        return rootView;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (rootView != null) {
            mIsFromCache = true;
            return rootView;
        } else {
            rootView = getView();
            if (rootView == null) {
                rootView = inflater.inflate(provideLayoutResourceId(), container, false);
                performBindings(rootView);
                mIsFromCache = false;
            } else {
                mIsFromCache = true;
            }
        }
        Log.d(TAG, "onCreateView " + mIsFromCache);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "setViewBehaviour, cache: " + mIsFromCache);
        setViewBehaviour(mIsFromCache);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    // tag::FRG-5.1[]
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // tag::ignore[]
        Log.d(TAG, "onCreate");
        // end::ignore[]
        super.onCreate(savedInstanceState);
        // tag::ignore[]
        Log.d(TAG, "onReady");
        onReady();
        // end::ignore[]
        // tag::ignore[]
        if (getArguments() != null) {
            extractArguments();
        }
        // end::ignore[]
    }

    @Override
    public void onAttach(Context context) {
        // tag::ignore[]
        Log.d(TAG, "onAttach");
        // end::ignore[]
        super.onAttach(context);
        // tag::ignore[]
        this.fragmentContext = context;
        // end::ignore[]
        this.hostActivity = (ParentActivityImpl) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        this.fragmentContext = null;
        this.rootView = null;
        super.onDetach();
    }

    // tag::FRG-5.2[]
    @Override
    public void onDestroy() {
        // tag::ignore[]
        Log.d(TAG, "onDestroy");
        // end::ignore[]
        super.onDestroy();
    }
    // end::FRG-5.2[]

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }
}
