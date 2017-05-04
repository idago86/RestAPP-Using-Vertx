/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.main;

import io.vertx.core.Vertx;

/**
 *
 * @author Israel Dago
 */
public class AppLauncher {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("com.idago.productsva.vertx.RestApp");        
    }

}
