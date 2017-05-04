/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.dao;

import com.idago.productsva.entites.db.ProductDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Israel Dago
 */
public class ProductDAO implements CRUD<ProductDB> {

    private final Connection DBCon;

    public ProductDAO(Supplier<Connection> SQLCon) {
        this.DBCon = SQLCon.get();
    }

    @Override
    public Integer create(ProductDB product) {
        try (PreparedStatement createQuery = DBCon.prepareStatement("INSERT INTO `products` VALUES (NULL,?,?,?,?)");
                PreparedStatement searchQuery = DBCon.prepareStatement("SELECT * FROM `products` WHERE registeredDate =? ")) {
            createQuery.setString(1, product.getName());
            createQuery.setString(2, product.getDescription());
            createQuery.setDouble(3, product.getPrice());
            createQuery.setString(4, product.getRegisteredDate());
            int createQueryResult = createQuery.executeUpdate();
            if (createQueryResult == 1) {
                searchQuery.setString(1, product.getRegisteredDate());
                ResultSet rs = searchQuery.executeQuery();
                return rs.next() ? rs.getInt("id") : 0;
            };
        } catch (SQLException ex) {
            logError("Oooops...ERROR in create Method ==> ", ex);
        }
        return 0;
    }

    @Override
    public ProductDB findOne(Integer productID) {
        try (PreparedStatement searchQuery = DBCon.prepareStatement("SELECT * FROM `products` WHERE id =? ")) {
            searchQuery.setInt(1, productID);
            ResultSet rs = searchQuery.executeQuery();
            if (rs.next()) {
                return new ProductDB(rs.getInt("id"), rs.getString("name"),
                        rs.getString("description"), rs.getDouble("price"), rs.getString("registeredDate"));
            }
        } catch (SQLException ex) {
            logError("Error in findOne Method ==> ", ex);
        }
        return null;
    }

    @Override
    public Stream<ProductDB> findAll() {
        List<ProductDB> resultList = new ArrayList<>();
        try (PreparedStatement psQuery = DBCon.prepareStatement("SELECT * FROM `products`")) {
            ResultSet rs = psQuery.executeQuery();
            while (rs.next()) {
                resultList.add(new ProductDB(rs.getInt("id"), rs.getString("name"),
                        rs.getString("description"), rs.getDouble("price"), rs.getString("registeredDate"))
                );
            }
        } catch (SQLException ex) {
            logError("Ooops, Errror retrieving all products from DAO", ex);
        }
        return resultList.stream();

    }    

    @Override
    public Boolean remove(Integer id) {
        try (PreparedStatement deleteQuery = DBCon.prepareStatement("DELETE FROM `products` WHERE id=?")) {
            deleteQuery.setInt(1, id);
            int resultQuery = deleteQuery.executeUpdate();
            return resultQuery == 1;
        } catch (SQLException ex) {
            logError("Ooops, Errror in deleting product from DAO", ex);
            return false;
        }
    }

    @Override
    public ProductDB update(ProductDB product) {
        try (PreparedStatement updateQuery = DBCon.prepareStatement("UPDATE `products` SET name=?, description=?, price=? WHERE id=" + product.getId())) {
            updateQuery.setString(1, product.getName());
            updateQuery.setString(2, product.getDescription());
            updateQuery.setDouble(3, product.getPrice());
            int resultQuery = updateQuery.executeUpdate();
            return resultQuery == 1 ? findOne(product.getId()) : null;
        } catch (SQLException ex) {
            logError("Ooops, Errror in updating product from DAO", ex);
            return null;
        }
    }
    
    private void logError(String fancyMessage, Throwable ex) {
        Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, fancyMessage, ex);
    }
}
