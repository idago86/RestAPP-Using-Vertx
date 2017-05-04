/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Israel Dago
 */
public final class TransactionManager implements AutoCloseable {

    private final String URL, USER, PAROLA;
    private final ThreadLocal<Connection> CONNECTIONHOLDER;

    private TransactionManager() {
        URL = "jdbc:mysql://localhost/productsdb?serverTimezone=UTC";
        USER = "root";
        PAROLA = "12345";
        this.CONNECTIONHOLDER = new ThreadLocal<>();
    }

    @Override
    public void close() {
        if (this.CONNECTIONHOLDER.get() != null) {
            try {
                release();
            } catch (SQLException ex) {
                Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class SingletonHolder {

        private final static TransactionManager SINGLETON = new TransactionManager();
    }

    public static TransactionManager getInstance() {
        return SingletonHolder.SINGLETON;
    }

    public void begin() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            Connection con = this.getConnection();
            con.setAutoCommit(false);
            this.CONNECTIONHOLDER.set(con); 
        } catch (ClassNotFoundException e) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void commit() throws SQLException {
        this.CONNECTIONHOLDER.get().commit();
    }

    public void rollback() throws SQLException {
        this.CONNECTIONHOLDER.get().rollback();
    }

    private void release() throws SQLException {
        this.CONNECTIONHOLDER.get().commit();
        this.CONNECTIONHOLDER.get().close();
    }

    public Connection getConnection() {        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PAROLA);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
