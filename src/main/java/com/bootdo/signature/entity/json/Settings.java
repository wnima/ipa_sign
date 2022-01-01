package com.bootdo.signature.entity.json;
import java.util.List;

public class Settings {

    private String key;
    private List<Options> options;
    public void setKey(String key) {
         this.key = key;
     }
     public String getKey() {
         return key;
     }

    public void setOptions(List<Options> options) {
         this.options = options;
     }
     public List<Options> getOptions() {
         return options;
     }

}