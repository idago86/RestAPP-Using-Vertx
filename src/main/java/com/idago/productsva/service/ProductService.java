/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.service;

import com.idago.productsva.dao.ProductDAO;
import com.idago.productsva.entites.db.ProductDB;
import com.idago.productsva.entites.dto.ProductDTO;
import com.idago.productsva.transaction.TransactionManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Israel Dago
 */
public class ProductService implements ServicesExposed {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO(() -> TransactionManager.getInstance().getConnection());
    }

    @Override
    public Integer create(ProductDTO product) {
        Integer createdProductID = 0; 
        try (TransactionManager transaction = TransactionManager.getInstance()) {
            transaction.begin();
            if (product.getRegisteredDate() != null) {
                createdProductID = productDAO.create(mapToDB(product));
                transaction.commit();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createdProductID;
    }

    @Override
    public ProductDTO findOneProduct(Integer id) {
        ProductDB retrievedDB = productDAO.findOne(id);
        return retrievedDB != null ? mapToDTO(retrievedDB) : null;
    }

    @Override
    public Stream<ProductDTO> findAllProducts() {
        return productDAO.findAll().map(this::mapToDTO);
    }

    @Override
    public Boolean remove(Integer id) {
        Boolean flag = false;
        try (TransactionManager transaction = TransactionManager.getInstance()) {
            transaction.begin();
            ProductDB retrievedDB = productDAO.findOne(id);
            if (retrievedDB != null) {
                flag = productDAO.remove(retrievedDB.getId());
                transaction.commit();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
            flag = false;
        }
        return flag;
    }

    @Override
    public ProductDTO update(ProductDTO product) {
        try (TransactionManager transaction = TransactionManager.getInstance()) {
            transaction.begin();
            ProductDB retrievedDB = productDAO.findOne(product.getId());
            if (retrievedDB != null) {
                retrievedDB = productDAO.update(mapToDB(product));
                transaction.commit();
                return mapToDTO(retrievedDB);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static class SingletonHolder {
        private final static ProductService SINGLETON = new ProductService();
    }

    public static ProductService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    private ProductDB mapToDB(ProductDTO dto) {
        return dto.getId() == null ? 
                  new ProductDB(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getRegisteredDate())
                : new ProductDB(dto.getId(), dto.getName(), dto.getDescription(), dto.getPrice(), dto.getRegisteredDate());
    }

    private ProductDTO mapToDTO(ProductDB db) {
        return new ProductDTO(db.getId(), db.getName(), db.getDescription(), db.getPrice(), db.getRegisteredDate());
    }

}
