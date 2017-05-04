/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idago.productsva.vertx;

import com.idago.productsva.entites.dto.ProductDTO;
import com.idago.productsva.service.ProductService;
import com.idago.productsva.service.ServicesExposed;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Israel Dago
 */
public class RestApp extends AbstractVerticle {

    private final ServicesExposed service;
    private final Vertx appVertx;
    private final Logger logger;

    public RestApp() {
        this.service = ProductService.getInstance();
        this.appVertx = Vertx.vertx();
        this.logger = LoggerFactory.getLogger(RestApp.class); 
    }

    @Override
    public void start() {
        ///creeate a router
        Router router = Router.router(appVertx);

        ///Allow the router to retrieve the body of any request made
        router.route().handler(BodyHandler.create());

        ///create the endpoints
        router.get("/").handler(routingCtx -> routingCtx.reroute("/products"));
        router.get("/products").handler(this::all);
        router.get("/products/:productID").handler(this::findOne);
        router.post("/products").handler(this::create);
        router.delete("/products/:productID").handler(this::deleteOne);
        router.put("/products/:productID").handler(this::updateOne);

        //RxHelper helps to create an handler who will logger the connection state
        ObservableFuture<HttpServer> rxServer = RxHelper.observableFuture();
        rxServer.subscribe(
                server -> logger.info("Server is ready and listening on Port " + server.actualPort()), 
                error -> logger.error("Oooops, Server could not start : Cause => " + error.getMessage(), error.getCause())     
        );

        //finaly creating an httpServer
        HttpServerOptions config = new HttpServerOptions().setPort(8099); //// will configure the server on a specific port
        appVertx.createHttpServer(config)
                .requestHandler(router::accept) /// let the router be in charge of the requests
                .listen(rxServer.toHandler()); 
    }

    private void all(RoutingContext ctx) {
        List<JsonObject> allProducts = service.findAllProducts()
                .map(JsonObject::mapFrom)
                .map(jsObj -> jsObj.put("self", ctx.request().absoluteURI() + jsObj.getInteger("id")))
                .collect(toList());
        ctx.response()
                .putHeader("Content-type", "application/json : charset=utf-8")
                .setStatusMessage(allProducts.isEmpty() ? "No Content" : "Content receive with success")
                .setStatusCode(allProducts.isEmpty() ? 204 : 200)
                .end(Json.encodePrettily(allProducts));
    }

    private void findOne(RoutingContext ctx) {
        String productID = ctx.request().getParam("productID");

        logger.info("Search Querry with productID " + productID + " => from DeploymentID " + this.deploymentID());

        if (parseToIntegerIfNumber(productID) != null) {
            ProductDTO retrievedProduct = service.findOneProduct(parseToIntegerIfNumber(productID));
            JsonObject customJsonProduct = JsonObject.mapFrom(retrievedProduct)
                    .put("self", ctx.request().absoluteURI())
                    .put("allProducts", ctx.request().host() + "/products/");
            Boolean flag = customJsonProduct == null;
            ctx.response()
                    .putHeader("Content-type", "application/json : charset=utf-8")
                    .setStatusMessage(retrievedProduct == null ? "No Content" : "Content receive with success")
                    .setStatusCode(flag ? 204 : 200)
                    .end(flag ? Json.encode("No Content found") : Json.encodePrettily(customJsonProduct));
        } else {
            ctx.response()
                    .putHeader("Content-type", "text/plain : charset=utf-8")
                    .setStatusCode(500)
                    .end("productID is not a valid Number");
        }
    }

    private void create(RoutingContext ctx) {
        String productToCreateAsString = ctx.getBodyAsString();
        ProductDTO productDTO = Json.decodeValue(productToCreateAsString, ProductDTO.class);
        productDTO.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        Integer productID = service.create(productDTO);
        if (productID != 0) {
            ctx.response()
                    .putHeader("Content-type", "application/json : charset=utf-8")
                    .setStatusCode(201)
                    .end(Json.encodePrettily(service.findOneProduct(productID)));
        } else {
            ctx.response()
                    .putHeader("Content-type", "text/plain : charset=utf-8")
                    .setStatusCode(500)
                    .end("Product Not Created");
        }
    }

    private void updateOne(RoutingContext ctx) {
        String productToCreateAsString = ctx.getBodyAsString();
        ProductDTO productDTO = Json.decodeValue(productToCreateAsString, ProductDTO.class);
        Integer productID = parseToIntegerIfNumber(ctx.request().getParam("productID"));
        if (productID == null) {
            ctx.response()
                    .putHeader("Content-type", "text/plain : charset=utf-8")
                    .setStatusCode(400)
                    .end("productID is not a valid Number");
        } else {
            productDTO.setId(productID);
            productDTO = service.update(productDTO);
            if (productDTO != null) {
                ctx.response()
                        .putHeader("Content-type", "text/plain : charset=utf-8")
                        .setStatusCode(202)
                        .end("Product Updated Successfully");
            } else {
                ctx.response()
                        .putHeader("Content-type", "text/plain : charset=utf-8")
                        .setStatusCode(500)
                        .end("Product Not Updated");
            }
        }
    }

    private void deleteOne(RoutingContext ctx) {
        String param_id = ctx.request().getParam("productID");
        logger.info("Delete Querry with productID " + param_id + " => from DeploymentID " + this.deploymentID());  
        Integer productID = parseToIntegerIfNumber(param_id);
        if (productID != null) {
            Boolean flag = service.remove(productID);
            ctx.response()
                    .putHeader("Content-type", "text/plain : charset=utf-8")
                    .setStatusCode(flag ? 200 : 501)
                    .end(flag ? "Content deleted with success" : "Request Failed");
        } else {
            ctx.response()
                    .putHeader("Content-type", "text/plain : charset=utf-8")
                    .setStatusCode(400)
                    .end("productID is not a valid Number");
            logger.error("Delete Request with invalid productID ==> Not a valid Number");  
        }
    }

    private Integer parseToIntegerIfNumber(String numberString) {
        try {
           return Integer.parseInt(numberString); 
        } catch (NumberFormatException e) {
            logger.error("Error on parseToIntegerIfNumber() Method ==> invalid number received", e.getMessage()); 
           return null;  
        }
    }
}