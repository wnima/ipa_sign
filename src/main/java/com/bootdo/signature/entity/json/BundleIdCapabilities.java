package com.bootdo.signature.entity.json;
import java.util.List;

public class BundleIdCapabilities {

    private List<Data> data;
    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

}