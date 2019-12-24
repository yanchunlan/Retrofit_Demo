package com.example.library;

import androidx.annotation.Nullable;

import java.io.IOException;

abstract class ParameterHandler<T> {

    abstract void apply(RequestBuilder builder, @Nullable String value) throws IOException;

    static final class Query<T> extends ParameterHandler<T> {
        private final String name;

        Query(String name) {
            if (name.isEmpty()) {
                throw new IllegalArgumentException("name == null");
            }
            this.name =name;
        }

        @Override void apply(RequestBuilder builder, @Nullable String value) throws IOException {
            if (value == null) return; // Skip null values.
            builder.addQueryParam(name, value);
        }
    }

    static final class Field<T> extends ParameterHandler<T> {
        private final String name;

        Field(String name) {
            if (name.isEmpty()) {
                throw new IllegalArgumentException("name == null");
            }
            this.name = name;
        }

        @Override void apply(RequestBuilder builder, @Nullable String value) throws IOException {
            if (value == null) return; // Skip null values.
            builder.addFormField(name, value);
        }
    }

}