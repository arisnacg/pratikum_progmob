package com.example.gusarisna.pratikum.data.remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://192.168.42.182:8000/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
