package com.erastus.orientate.utils;

public class BPQItem<T> {
    T item;
    Double dist;

    public BPQItem(T item, Double dist) {
        this.item = item;
        this.dist = dist;
    }
}