package com.example.pc.appmusic.Service;

public class APIService {
    private static String base_url = "https://qhnstore.000webhostapp.com/Server/";

    public static Dataservice getService()
    {
        return APIRetrofitClient.getClient(base_url).create(Dataservice.class);
    }
}
