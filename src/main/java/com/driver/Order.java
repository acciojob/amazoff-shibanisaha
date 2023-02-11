package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

          this.id = id;
          String hr = deliveryTime.substring(0,2);
          String mn = deliveryTime.substring(3);
          this.deliveryTime = Integer.parseInt(hr)*60+Integer.parseInt(mn);
        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
