package com.example.gusarisna.pratikum.data.remote;

public class ApiUtils {

    private ApiUtils() {}

    //public static final String BASE_URL = "http://172.17.100.2:8000/";
    public static final String BASE_URL = "http://pratikum.dewatamemoriestours.com/";
    //public static final String BASE_URL = "http://192.168.42.99:8000/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL+"api/").create(APIService.class);
    }
}
