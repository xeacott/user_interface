package com.example.user_interface;


public class Analytics {

    public Analytics(String data, int id) throws Exception {
        this.data = data;
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public Integer getId() {
        return id;
    }

    // PRIVATE

    private final String data;
    private final Integer id;
}