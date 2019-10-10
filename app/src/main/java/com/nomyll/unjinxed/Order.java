package com.nomyll.unjinxed;

public class Order {
    String nameOwner;
    double totalPrice;

    public Order() {
    }

    public String getNameOwner() {
        return nameOwner;
    }

    public void setNameOwner(String nameOwner) {
        this.nameOwner = nameOwner;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return nameOwner + " | R$: " + totalPrice;
    }
}
