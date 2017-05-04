/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.entites.dto;

/**
 *
 * @author Israel Dago
 */
public class ProductDTO implements java.io.Serializable{
    private Integer id;
    private String name, description;
    private Double price;
    private String registeredDate;

    public ProductDTO() {
    }
    
    public ProductDTO(String name, String description, Double price, String registeredDate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.registeredDate = registeredDate;
    }

    public ProductDTO(Integer id, String name, String description, Double price, String registeredDate) {
        this(name, description, price, registeredDate);
        this.id = id;        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    @Override
    public String toString() {
        return "DTO => "+this.id +" " + this.name +" " + this.description +" " + this.price +" " + this.registeredDate ;
    }
    
    
}
