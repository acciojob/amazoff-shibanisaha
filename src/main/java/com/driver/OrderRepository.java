package com.driver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class OrderRepository {

     private HashMap<String, Order> OrderDB;
     private HashMap<String, DeliveryPartner> DeliveryPartnerDB;
     private HashMap<String, List<String>> OrderPartnerPairDB;
     private HashSet<String> notAssignedOrderDB;

     public OrderRepository(){
         this.OrderDB = new HashMap<>();
         this.DeliveryPartnerDB = new HashMap<>();
         this.OrderPartnerPairDB = new HashMap<>();
         this.notAssignedOrderDB = new HashSet<>();
     }


    public void addOrder(Order order){
        OrderDB.put(order.getId(), order);
        notAssignedOrderDB.add(order.getId());
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        DeliveryPartnerDB.put(partnerId, deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
         DeliveryPartnerDB.get(partnerId).setNumberOfOrders(DeliveryPartnerDB.get(partnerId).getNumberOfOrders()+1);

         if(OrderPartnerPairDB.containsKey(partnerId)){
             List<String> list = OrderPartnerPairDB.get(partnerId);
             list.add(orderId);
             notAssignedOrderDB.remove(orderId);
             return;
         }

         OrderPartnerPairDB.put(partnerId, new ArrayList<>(Arrays.asList(orderId)));
         notAssignedOrderDB.remove(orderId);

    }

    public Order getOrderById(String orderId){
       return OrderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return DeliveryPartnerDB.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
       return OrderPartnerPairDB.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return OrderPartnerPairDB.get(partnerId);
        //orders should contain a list of orders by PartnerId

    }

    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for(String s: OrderDB.keySet()){
            orders.add(s);
        }
        return orders;
    }

    public int getCountOfUnassignedOrders(){
       return notAssignedOrderDB.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        String hr = time.substring(0,2);
        String mn = time.substring(3,5);
        int lt = Integer.parseInt(hr)*60+Integer.parseInt(mn);
        int count = 0;
        if(OrderPartnerPairDB.containsKey(partnerId)){
            for(String s: OrderPartnerPairDB.get(partnerId)){
                if(OrderDB.get(s).getDeliveryTime() > lt){
                    count++;
                }
            }
        }

        return count;
        //countOfOrders that are left after a particular time of a DeliveryPartner

    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int t =0;

        //Return the time when that partnerId will deliver his last delivery order.
        if (OrderPartnerPairDB.containsKey(partnerId)) {
            for(String e: OrderPartnerPairDB.get(partnerId)){
                t = OrderDB.get(e).getDeliveryTime();
            }

        }
        String hr = Integer.toString(t/60);
        String mn = Integer.toString(t%60);
        if(hr.length()==1){
            hr = "0"+hr;
        }
        if(mn.length()==1){
            mn="0"+mn;
        }

        return hr+":"+mn;
    }

    public void deletePartnerById(String partnerId){

        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
       if(!OrderPartnerPairDB.isEmpty()){
           notAssignedOrderDB.addAll(OrderPartnerPairDB.get(partnerId));
       }
       OrderPartnerPairDB.remove(partnerId);
       DeliveryPartnerDB.remove(partnerId);
    }

    public  void deleteOrderById(String orderId){

        //Delete an order and also
        // remove it from the assigned order of that partnerId

        OrderDB.remove(orderId);
        if(notAssignedOrderDB.contains(orderId)){
            notAssignedOrderDB.remove(orderId);
        }else{
            for(List<String> list : OrderPartnerPairDB.values()){
                list.remove(orderId);
            }
        }
    }


}
