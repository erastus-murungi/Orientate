package com.erastus.orientate.student.models;

import org.jetbrains.annotations.NotNull;

public class DataState<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private DataState() {
    }

    @NotNull
    @Override
    public String toString() {
        if (this instanceof DataState.Success) {
            DataState.Success<T> success = (DataState.Success<T>) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof DataState.Error) {
            DataState.Error error = (DataState.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends DataState<T> {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends DataState<String> {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
