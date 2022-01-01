package com.bootdo.signature.entity.json;
import java.util.List;

public class Attributes {

    private List<Settings> settings;

    public void setSettings(List<Settings> settings) {
         this.settings = settings;
     }
     public List<Settings> getSettings() {
         return settings;
     }

}