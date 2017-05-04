/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.dao;

import java.util.stream.Stream;

/**
 *
 * @author Israel Dago
 * @param <E> entity to persist
 */
public interface CRUD<E> {
    Integer create(E entity);
    Boolean remove(Integer id);
    E update(E entity); 
    E findOne(Integer id);
    Stream<E> findAll();
}
