package kaist.restaurantownerapp.data.handler;


import com.google.gson.Gson;

import kaist.restaurantownerapp.data.*;

public class RestaurantInfoJsonSerializer {

    public static RestaurantInfo deserialize(String json){
        Gson gson = new Gson();
        RestaurantInfo resInfo = gson.fromJson(json,RestaurantInfo.class);
        return resInfo;
    }

    public static String serialize(RestaurantInfo info){
        Gson gson = new Gson();
        String json = gson.toJson(info);
        return json;
    }
}
