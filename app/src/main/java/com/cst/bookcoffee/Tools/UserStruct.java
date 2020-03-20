package com.cst.bookcoffee.Tools;

public class UserStruct {
    private String id;
    private String name;
    private String pinyin;
    public UserStruct(String id, String name, String pinyin){
        this.id=id;this.name=name;this.pinyin=pinyin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }
}
