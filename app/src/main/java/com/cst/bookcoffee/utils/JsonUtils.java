package com.cst.bookcoffee.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {
    /**
     * @param json  json数据
     * @return
     */
    public static List<String> getJsonList(String json) {
        List<String> dataList;
        dataList = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray feedsArray = rootObject.getJSONArray("words_result");
            for (int i = 0; i < feedsArray.length(); i++) {
                dataList.add(feedsArray.getJSONObject(i).getString("words"));
            }
            return dataList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
