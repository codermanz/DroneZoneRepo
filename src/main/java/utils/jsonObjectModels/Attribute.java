package utils.jsonObjectModels;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {

    public Attribute() {

    }

    public Attribute(String attribute_Name, String attribute_Value) {
        this.attribute_Name = attribute_Name;
        this.attribute_Value = attribute_Value;
    }

    private String attribute_Name;
    private String attribute_Value;

    @JsonProperty("label")
    public String getAttribute_Name() {
        return attribute_Name;
    }

    @JsonProperty("value")
    public String getAttribute_Value() {
        return attribute_Value;
    }

    public void setAttribute_Name(String attribute_Name) {
        this.attribute_Name = attribute_Name;
    }

    public void setAttribute_Value(String attribute_Value) {
        this.attribute_Value = attribute_Value;
    }

    @Override
    public String toString() {
        return ("\n\t\t\tAttribute name: " + this.attribute_Name + " value: " + this.attribute_Value);
    }
}
