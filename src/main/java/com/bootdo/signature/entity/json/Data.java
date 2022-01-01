package com.bootdo.signature.entity.json;

import java.util.Map;

public class Data {

    private String type;
    private Attributes attributes;
    private Map<String, Object> relationships;
    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setAttributes(Attributes attributes) {
         this.attributes = attributes;
     }
     public Attributes getAttributes() {
         return attributes;
     }

    public void setRelationships(Map<String, Object> relationships) {
         this.relationships = relationships;
     }
     public Map<String, Object> getRelationships() {
         return relationships;
     }

}