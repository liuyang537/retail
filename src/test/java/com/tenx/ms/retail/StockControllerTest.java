package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StockRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.service.ProductService;
import com.tenx.ms.retail.service.StockService;
import com.tenx.ms.retail.service.StoreService;
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
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class StockControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StockService stockService;

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testUpdateQuantityApi1() throws JsonProcessingException {//Reference case

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/" + sid + "/" + pid + "/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 200);

        productService.delete(pid, sid);
        storeService.delete(sid);
    }

    @Test
    public void testUpdateQuantityApi2() throws JsonProcessingException {//negative count

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", -5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/" + sid + "/" + pid + "/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        productService.delete(pid, sid);
        storeService.delete(sid);
    }

    @Test
    public void testUpdateQuantityApi3() throws JsonProcessingException {//no such store existed

        long sid = storeService.save(new Store("store1"));
        long originalSid = sid;

        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));

        Store s = storeService.findByID(sid);
        while (s != null) {
            sid = (long) (Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/" + sid + "/" + pid + "/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        productService.delete(pid, originalSid);
        storeService.delete(originalSid);
    }

    @Test
    public void testUpdateQuantityApi4() throws JsonProcessingException {//no such product in current store

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));
        long originalPid = pid;

        Product p = productService.findByIDAndStore_id(pid, sid);
        while (p != null) {
            pid = (long) (Math.random() * 100 + 1);
            p = productService.findByIDAndStore_id(pid, sid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/" + sid + "/" + pid + "/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        productService.delete(originalPid, sid);
        storeService.delete(sid);
    }

    @Test
    public void testDeleteProductApi() {//test delete product function with stock associate with such product

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));

        Store store = storeService.findByID(sid);
        assertNotNull(store);

        Product product = productService.findByIDAndStore_id(pid, sid);
        assertNotNull(product);
        stockService.set(store, product, (long)5);
        assertNotNull(stockService.get(product, store));

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(stockService.get(product, store));
        assertNull(productService.findByIDAndStore_id(pid, sid));

        storeService.delete(sid);
    }

    @Test
    public void testDeleteStoreApi() {//test delete product function with stock associate with such product

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product(sid, "product1", "description1", "abc123", 1.00));

        Store store = storeService.findByID(sid);
        assertNotNull(store);

        Product product = productService.findByIDAndStore_id(pid, sid);
        assertNotNull(product);
        stockService.set(store, product, (long)5);
        assertNotNull(stockService.get(product, store));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(stockService.get(product, store));
        assertNull(productService.findByIDAndStore_id(pid, sid));
        assertNull(storeService.findByID(sid));

    }
}

