package com.erastus.orientate.utils;

import androidx.annotation.NonNull;

public class BPQItem<T> {
    T item;
    Double dist;

    public BPQItem(T item, Double dist) {
        this.item = item;
        this.dist = dist;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + item + ", " + dist + ")";
    }
}