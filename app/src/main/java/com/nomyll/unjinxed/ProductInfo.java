package com.nomyll.unjinxed;

public class ProductInfo {
    String name;
    Double value;

    public ProductInfo() {
    }

    public void setData(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " | R$: " + value;
    }
}
