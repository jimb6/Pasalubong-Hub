package com.allandroidprojects.ecomsample.data.models;

public class DataResult<T> {
    @Override
    public String toString() {
        if (this instanceof DataResult.Add) {
            DataResult.Add added = (DataResult.Add) this;
            return "ADDED[data added=" + added.getData().toString() + "]";
        } else if (this instanceof DataResult.Modified) {
            DataResult.Modified modified = (DataResult.Modified) this;
            return "MODIFIED[ =" + modified.getData().toString() + "]";
        } else if (this instanceof DataResult.Removed){
            DataResult.Removed removed = (DataResult.Removed) this;
            return "REMOVED[ =" + removed.getData().toString() + "]";
        }else if (this instanceof DataResult.Error) {
            DataResult.Error error = (DataResult.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    // ADD Data Changes
    public final static class Add<T> extends DataResult {
        private T data;
        public Add(T data) {
            this.data = data;
        }
        public T getData() {
            return this.data;
        }
    }

    // MODIFIED Data Changes
    public final static class Modified<T> extends DataResult {
        private T data;
        public Modified(T data) {
            this.data = data;
        }
        public T getData() {
            return this.data;
        }
    }

    // REMOVED Data Changes
    public final static class Removed<T> extends DataResult {
        private T data;
        public Removed(T data) {
            this.data = data;
        }
        public T getData() {
            return this.data;
        }
    }

    // ERROR Data Changes
    public final static class Error extends DataResult {
        private Exception error;
        public Error(Exception error) {
            this.error = error;
        }
        public Exception getError() {
            return this.error;
        }
    }
}
