package com.example.layeredarchitecture.bo;

import com.example.layeredarchitecture.bo.custom.impl.CustomerBOImpl;
import com.example.layeredarchitecture.bo.custom.impl.ItemBOImpl;
import com.example.layeredarchitecture.bo.custom.impl.PlaceOrderBOImpl;

public class FactoryBO {
    private static FactoryBO factoryBO;

    private FactoryBO() {}

    public static FactoryBO getFactoryBO() {
        return factoryBO == null ? factoryBO = new FactoryBO() : factoryBO;
    }

    public enum BOTypes {
        CUSTOMER, ITEM, PLACE_ORDER
    }

    public SuperBO getBO(BOTypes boTypes) {
        switch (boTypes) {
            case CUSTOMER:
                return new CustomerBOImpl();
            case ITEM:
                return new ItemBOImpl();
            case PLACE_ORDER:
                return new PlaceOrderBOImpl();
            default:
                return null;
        }
    }
}
