package com.example.layeredarchitecture.dao.custom;

import com.example.layeredarchitecture.dao.SuperDAO;
import com.example.layeredarchitecture.model.CustomerDTO;
import javafx.scene.Scene;

public interface QueryDAO extends SuperDAO {
    void customerOrderDetails(CustomerDTO customerDTO);
}
