package communicationInventoryAndSupplier;

import java.util.List;

public class MutualOrder {
    private String orderID;
    private boolean isTkufati;
    private List<MutualItem> itemsDelivered;

    // if true then it is a request for an order, if false it is an order that has been delivered
    private boolean isRequest;

}


//
//    //in supplier module
//    public Status getOrderStatus(String OrderId )
//    {
//
//    }
//
//
//
//    // in inventory module:
//    public  void acceptOrder(Order o){
//
//    }
//
//    // main:
//
//    Order order = acceptOrder();
//
//    //lets update my DB:
//
//    while (true){
//     ITERATE OVER MUTUAL DB AND CHECK IF THERES NEW DATA

//        Thread.sleep(1 sec);
//        if (getOrderStatus() == true){
//            Order order = acceptOrder();
//        }
//
//    }
//
//}




