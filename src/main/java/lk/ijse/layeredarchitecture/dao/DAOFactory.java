package lk.ijse.layeredarchitecture.dao;

import lk.ijse.layeredarchitecture.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory DAOFactory;

    private DAOFactory(){}

    public static DAOFactory getFactoryDAO() {
        return DAOFactory == null ? DAOFactory = new DAOFactory() : DAOFactory;
    }

    public enum DAOTypes {
        CUSTOMER, ITEM, ORDER, ORDER_DETAIL, QUERY
    }

    public SuperDAO getDAO(DAOTypes daoTypes) {
        switch (daoTypes) {
            case CUSTOMER:
                return new CustomerDAOImpl();
            case ITEM:
                return new ItemDAOImpl();
            case ORDER:
                return new OrderDAOImpl();
            case ORDER_DETAIL:
                return new OrderDetailsDAOImpl();
            case QUERY:
                return new QueryDAOImpl();
            default:
                return null;
        }
    }
}
