package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.entity.OrderStatus;
import com.tenx.ms.retail.entity.OrderedProduct;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StockRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class OrderControllerTest {

    //Directly invoke the APIs interaction with the DB
    private ProductRepository pr;
    private StoreRepository sr;
    private StockRepository tr;
    private OrderRepository or;

    public OrderControllerTest() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(StoreRepository.class);
        this.sr = ctx1.getBean(StoreRepository.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(ProductRepository.class);
        this.pr = ctx2.getBean(ProductRepository.class);
        ApplicationContext ctx3 = new AnnotationConfigApplicationContext(StockRepository.class);
        this.tr = ctx3.getBean(StockRepository.class);
        ApplicationContext ctx4 = new AnnotationConfigApplicationContext(OrderRepository.class);
        this.or = ctx4.getBean(OrderRepository.class);
    }

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testCreateOrderApi1()throws JsonProcessingException{//Reference case, if there is something that needs backorder

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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
        assertEquals(apiResponse.getStatus(), "ORDERED");

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi2()throws JsonProcessingException{//Reference case, if nothing needs backorder

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi3()throws JsonProcessingException{//checking for input validation, reference case

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi4()throws JsonProcessingException{//checking for input validation, null first name

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi5()throws JsonProcessingException{//checking for input validation, invalid email address

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi6()throws JsonProcessingException{//checking for input validation, invalid phone number

        long sid = sr.addStore("store1");
        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(sid);
    }

    @Test
    public void testCreateOrderApi7() throws JsonProcessingException{//Bad Request, no such store existed

        long sid = sr.addStore("store1");
        long originalSid = sid;
        Store s = sr.findAStore(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = sr.findAStore(sid);
        }

        Map<Long, Long> orderProducts = new HashMap<Long, Long>();
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);
        orderProducts.put(pid1, (long)5);
        orderProducts.put(pid2, (long)6);

        long pid = 1;
        while(orderProducts.containsKey(pid)){
            pid = (long)(Math.random() * 100 + 1);
        }
        orderProducts.put(pid,(long)5);

        tr.updateQuantity(sid, pid1, 5, true);
        tr.updateQuantity(sid, pid2, 5, true);

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

        sr.deleteStore(originalSid);
    }

    @Test
    public void testDeleteProductApi(){//test delete product function with stock associate with such product

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        tr.updateQuantity(sid, pid, (long)5, true);
        List<OrderedProduct> ops = new ArrayList<>();
        OrderedProduct op = new OrderedProduct();
        op.setProduct_id(pid);
        op.setAmount(5);
        ops.add(op);
        LocalDate date = LocalDate.now();
        or.createOrder(sid, date, ops, "firstName", "lastName", "abc@gmail.com", "1234567890");

        assertNotNull(pr.findAProduct(sid, pid));

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(pr.findAProduct(sid, pid));

        sr.deleteStore(sid);
    }

    @Test
    public void testDeleteStoreApi(){//test delete product function with stock associate with such product

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        tr.updateQuantity(sid, pid, (long)5, true);
        List<OrderedProduct> ops = new ArrayList<>();
        OrderedProduct op = new OrderedProduct();
        op.setProduct_id(pid);
        op.setAmount(5);
        ops.add(op);
        LocalDate date = LocalDate.now();
        or.createOrder(sid, date, ops, "firstName", "lastName", "abc@gmail.com", "1234567890");

        assertNotNull(sr.findAStore(sid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(sr.findAStore(sid));
    }
}
