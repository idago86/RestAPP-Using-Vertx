/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.service;

import com.idago.productsva.entites.dto.ProductDTO;
import java.util.stream.Stream;

/**
 *
 * @author Israel Dago
 */
public interface ServicesExposed {
    Integer create(ProductDTO product);
    Boolean remove(Integer id);
    ProductDTO update(ProductDTO entity);
    ProductDTO findOneProduct(Integer id);
    Stream<ProductDTO> findAllProducts();
}
