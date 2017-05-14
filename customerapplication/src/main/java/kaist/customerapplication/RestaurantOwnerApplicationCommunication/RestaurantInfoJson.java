package kaist.customerapplication.RestaurantOwnerApplicationCommunication;


import com.google.gson.Gson;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;

class RestaurantInfoJson {

    private String json;
    private Menu menu;

    public RestaurantInfoJson(String json){
        this.json = json;
    }

    public RestaurantInfoJson(Menu menu){
        this.menu = menu;
    }

    public RestaurantInfo deserializeToRestaurantInfo(){
        RestaurantInfo info = new RestaurantInfo();
        info.menu = new Menu();
        Gson gson = new Gson();
        RestaurantInfo resInfo = gson.fromJson(json,RestaurantInfo.class);

        return resInfo;
    }

    public String serializeToJson(){
        Gson gson = new Gson();
        String json = gson.toJson(menu);

        return json;
    }

    public String getJson() {
        return json;
    }
}
