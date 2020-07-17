package com.erastus.orientate.student.announcements.models;

public class DataState<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private DataState() {
    }

    @Override
    public String toString() {
        if (this instanceof DataState.Success) {
            DataState.Success success = (DataState.Success) this;
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
    public final static class Error extends DataState {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
