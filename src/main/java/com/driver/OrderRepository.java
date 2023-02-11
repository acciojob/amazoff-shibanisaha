package com.driver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class OrderRepository {

     HashMap<String, Order> OrderDB= new HashMap<>();
     HashMap<String, DeliveryPartner> DeliveryPartnerDB = new HashMap<>();
     HashMap<String, List<Order>> addOrderPartnerPairDB = new HashMap<>();
     HashMap<String, String> assignedOrderDB = new HashMap<>();


    public void addOrder(Order order){
        OrderDB.put(order.getId(), order);
//        return "New order added successfully";
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        DeliveryPartnerDB.put(partnerId, deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        assignedOrderDB.put(orderId, partnerId);
        ArrayList<Order> list;
        //This is basically assigning that order to that partnerId
        if(addOrderPartnerPairDB.containsKey(partnerId)){
            list = new ArrayList<>(addOrderPartnerPairDB.get(orderId));
        }else{
            list = new ArrayList<>();
        }
        list.add(OrderDB.get(orderId));
        addOrderPartnerPairDB.put(partnerId, list);

    }

    public Order getOrderById(String orderId){
       return OrderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return DeliveryPartnerDB.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
       return addOrderPartnerPairDB.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders = new ArrayList<>();
        ArrayList<Order> list = new ArrayList<>(addOrderPartnerPairDB.get(partnerId));

        for(Order e: list){
            orders.add(e.getId());
        }
        return orders;
        //orders should contain a list of orders by PartnerId

    }

    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>(OrderDB.keySet());
        return orders;
    }

    public int getCountOfUnassignedOrders(){
       return OrderDB.size()-assignedOrderDB.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        String hr = time.substring(0,2);
        String mn = time.substring(3);
        int t = Integer.parseInt(hr)*60+Integer.parseInt(mn);
        ArrayList<Order> list = new ArrayList<>(addOrderPartnerPairDB.get(partnerId));
        int count = 0;
        for(Order e: list){
            if(e.getDeliveryTime()>t){
                count++;
            }
        }

        return t;
        //countOfOrders that are left after a particular time of a DeliveryPartner

    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int t =0;

        //Return the time when that partnerId will deliver his last delivery order.
        ArrayList<Order> list = new ArrayList<>(addOrderPartnerPairDB.get(partnerId));
        for(Order e: list){
            t = Math.max(t, e.getDeliveryTime());
        }
        String ans = Integer.toString(t/60)+":"+Integer.toString(t%60);
        return ans;
    }

    public void deletePartnerById(String partnerId){

        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        ArrayList<Order> list = new ArrayList<>(addOrderPartnerPairDB.get(partnerId));
        addOrderPartnerPairDB.remove(partnerId);
        DeliveryPartnerDB.remove(partnerId);
        for(Order e: list){
            assignedOrderDB.remove(e.getId());
        }
    }

    public  void deleteOrderById(String orderId){

        //Delete an order and also
        // remove it from the assigned order of that partnerId

        OrderDB.remove(orderId);
        String partnerid = assignedOrderDB.get(orderId);
        assignedOrderDB.remove(orderId);
        ArrayList<Order> list = new ArrayList<>(addOrderPartnerPairDB.get(partnerid));
        for(Order e: list){
            if(e.getId()==partnerid){
                list.remove(e);
            }
        }
        addOrderPartnerPairDB.put(partnerid, list);
    }


}
