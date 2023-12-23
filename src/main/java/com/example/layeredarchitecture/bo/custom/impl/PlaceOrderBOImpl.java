package com.example.layeredarchitecture.bo.custom.impl;

import com.example.layeredarchitecture.bo.custom.PlaceOrderBO;
import com.example.layeredarchitecture.dao.FactoryDAO;
import com.example.layeredarchitecture.dao.custom.*;
import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.CustomerDTO;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderBOImpl implements PlaceOrderBO {
    CustomerDAO customerDAO = (CustomerDAO) FactoryDAO.getFactoryDAO().getDAO(FactoryDAO.DAOTypes.CUSTOMER);
    ItemDAO itemDAO = (ItemDAO) FactoryDAO.getFactoryDAO().getDAO(FactoryDAO.DAOTypes.ITEM);
    OrderDAO orderDAO = (OrderDAO) FactoryDAO.getFactoryDAO().getDAO(FactoryDAO.DAOTypes.ORDER);
    OrderDetailsDAO orderDetailsDAO = (OrderDetailsDAO) FactoryDAO.getFactoryDAO().getDAO(FactoryDAO.DAOTypes.ORDER_DETAIL);
    QueryDAO queryDAO = (QueryDAO) FactoryDAO.getFactoryDAO().getDAO(FactoryDAO.DAOTypes.QUERY);

    @Override
    public boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {
        /*Transaction*/
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            boolean isExist = orderDAO.exist(orderId);
            if (isExist) {
                return false;
            }

            connection.setAutoCommit(false);
            boolean isSaved = orderDAO.save(new OrderDTO(orderId, orderDate, customerId));

            if (!isSaved) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            /*add details to the orderDetails table*/

            for (OrderDetailDTO detail : orderDetails) {
                boolean isOdSaved = orderDetailsDAO.save(detail);

                if (!isOdSaved) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }

//                //Search & Update Item
                ItemDTO item = findItem(detail.getItemCode());
                item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());

                boolean isUpdated = itemDAO.update(new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));

                if (!isUpdated) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.find(id);
    }

    @Override
    public ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException {
        return itemDAO.find(code);
    }

    @Override
    public boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return itemDAO.exist(code);
    }

    @Override
    public boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exist(id);
    }

    @Override
    public String generateOrderID() throws SQLException, ClassNotFoundException {
        return orderDAO.generateNewId();
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomer() throws SQLException, ClassNotFoundException {
        return customerDAO.getAll();
    }

    @Override
    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        return itemDAO.getAll();
    }

    public ItemDTO findItem(String code) {
        try {
            return itemDAO.find(code);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find the Item " + code, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
