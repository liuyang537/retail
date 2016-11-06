package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.domain.*;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StockRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.service.OrderService;
import com.tenx.ms.retail.service.ProductService;
import com.tenx.ms.retail.service.StockService;
import com.tenx.ms.retail.service.StoreService;
import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.*;
import static org.junit.Assert.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class OrderControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StockService stockService;

    @Autowired
    private OrderService orderService;

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testCreateOrderApi1()throws JsonProcessingException{//Reference case, if there is something that needs backorder

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        OrderStatus apiResponse = restTemplate.postForObject("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, OrderStatus.class, sid);

        System.out.println(apiResponse.toString());

        assertNotNull(apiResponse);
        assertEquals(apiResponse.getStatus(), "ORDERED");

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi2()throws JsonProcessingException{//Reference case, if nothing needs backorder

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        OrderStatus apiResponse = restTemplate.postForObject("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, OrderStatus.class, sid);

        assertNotNull(apiResponse);
        assertEquals(apiResponse.getStatus(), "PACKING");

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi3()throws JsonProcessingException{//checking for input validation, reference case

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 200);

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi4()throws JsonProcessingException{//checking for input validation, null first name

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi5()throws JsonProcessingException{//checking for input validation, invalid email address

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abcgmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi6()throws JsonProcessingException{//checking for input validation, invalid phone number

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","12345678901");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testCreateOrderApi7() throws JsonProcessingException{//Bad Request, no such store existed

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        long originalSid = sid;

        Store s = storeService.findByID(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","12345678901");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        storeService.delete(originalSid);
    }

    @Test
    public void testDeleteProductApi(){//test delete product function with stock associate with such product

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        restTemplate.postForObject("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, OrderStatus.class, sid);

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(productService.findByIDAndStore_id(pid, sid));

        storeService.delete(sid);
    }

    @Test
    public void testDeleteStoreApi(){//test delete product function with stock associate with such product

        long sid = storeService.save(new Store("store1"));
        Store store = storeService.findByID(sid);

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        Product product1 = productService.findByIDAndStore_id(pid1, sid);
        Product product2 = productService.findByIDAndStore_id(pid2, sid);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        stockService.set(store, product1, (long)5);
        stockService.set(store, product2, (long)5);

        //Building the Request body data
        JSONObject request = new JSONObject();
        JSONArray orderProductsArray = new JSONArray();
        for(Long l : orderProducts.keySet()){
            JSONObject orderProductJson = new JSONObject();
            orderProductJson.put("product_id", l);
            orderProductJson.put("amount", orderProducts.get(l));
            orderProductsArray.add(orderProductJson);
        }
        request.put("orderedProducts",orderProductsArray);
        request.put("first_name","firstName");
        request.put("last_name","lastName");
        request.put("email","abc@gmail.com");
        request.put("phone","1234567890");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        restTemplate.postForObject("http://localhost:8080/retail/v1/orders/{storeId}/", httpEntity, OrderStatus.class, sid);

        assertNotNull(storeService.findByID(sid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(storeService.findByID(sid));
    }
}
