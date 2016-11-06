package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.service.ProductService;
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
import java.util.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ProductControllerTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testAddProductApi() throws JsonProcessingException {//Reference case

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        Long apiResponse = restTemplate.postForObject("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, Long.class, sid);
        assertNotNull(apiResponse);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi1() throws JsonProcessingException {//Reference case

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 200);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi2() throws JsonProcessingException {//no such store exist

        long sid = storeService.save(new Store("store1"));
        long originalSid = sid;

        Store s = storeService.findByID(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        storeService.delete(originalSid);
    }

    @Test
    public void testAddProductApi3() throws JsonProcessingException {//duplicate product name

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 409);

        productService.delete(pid, sid);
        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi4() throws JsonProcessingException {//duplicate product sku

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product2");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "abc123");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 409);

        productService.delete(pid, sid);
        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi5() throws JsonProcessingException {//product name is null

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi6() throws JsonProcessingException {//invalid sku, length is too short

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi7() throws JsonProcessingException {//invalid sku, length is too long

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "1234567890a");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi8() throws JsonProcessingException {//invalid sku, invalid character presented

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.00);
        request.put("sku", "123.*abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testAddProductApi9() throws JsonProcessingException {//invalid price, too much percision

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product1");
        request.put("description", "description");
        request.put("price", 1.234);
        request.put("sku", "123abc");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/products/{storeId}/", httpEntity, String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

        storeService.delete(sid);
    }

    @Test
    public void testListAllProductsApi1() {//Reference case

        long sid = storeService.save(new Store("store1"));
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));

        //Invoke the API
        List<Product> apiResponse = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/", List.class, sid);

        assertNotNull(apiResponse);

        assertTrue(apiResponse.size() == 2);

        storeService.delete(sid);
    }

    @Test
    public void testListAllProductsApi2() {//no such store existed

        long sid = storeService.save(new Store("store1"));
        long originalSid = sid;
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));

        Store s = storeService.findByID(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        //Invoke the API
        ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/", String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        storeService.delete(originalSid);
    }

    @Test
    public void testListAllProductsApi3() {//No products in this store

        long sid = storeService.save(new Store("store1"));

        //Invoke the API
        ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/", String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 204);

        storeService.delete(sid);
    }

    @Test
    public void testFindAProduct1(){//Reference case

        long sid = storeService.save(new Store("store1"));
        long pid1 = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        long pid2 = productService.save(new Product( sid, "product2", "description2", "123abc", 1.00 ));

        Product apiResponse1 = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid1, Product.class, sid);

        assertNotNull(apiResponse1);
        assertEquals(apiResponse1.getProduct_id(), pid1);
        assertEquals(apiResponse1.getName(), "product1");

        Product apiResponse2 = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/?name=product2", Product.class, sid);
        assertNotNull(apiResponse2);
        assertEquals(apiResponse2.getProduct_id(), pid2);
        assertEquals(apiResponse2.getName(), "product2");

        storeService.delete(sid);
    }

    @Test
    public void testFindAProduct2() {//No such store existed

        long sid = storeService.save(new Store("store1"));
        long originalSid = sid;

        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));

        Store s = storeService.findByID(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        ResponseEntity<String> apiResponse1 = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, String.class, sid);
        HttpStatus status1 = apiResponse1.getStatusCode();
        assertEquals(status1.value(), 400);

        storeService.delete(originalSid);
    }

    @Test
    public void testFindAProduct3() {

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));

        Product p = productService.findByIDAndStore_id(pid, sid);
        while(p != null){
            pid = (long)(Math.random() * 100 + 1);
            p = productService.findByIDAndStore_id(pid, sid);
        }

        ResponseEntity<String> apiResponse1 = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, String.class, sid);
        HttpStatus status1 = apiResponse1.getStatusCode();
        assertEquals(status1.value(), 404);

        ResponseEntity<String> apiResponse2 = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/?name=product2", String.class, sid);
        HttpStatus status2 = apiResponse2.getStatusCode();
        assertEquals(status2.value(), 404);

        storeService.delete(sid);
    }

    @Test
    public void testDeleteProductApi() {

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        assertNotNull(productService.findByIDAndStore_id(pid, sid));

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(productService.findByIDAndStore_id(pid, sid));

        storeService.delete(sid);
    }

    @Test
    public void testDeleteStoreApi(){//test delete store function with products associated

        long sid = storeService.save(new Store("store1"));
        long pid = productService.save(new Product( sid, "product1", "description1", "abc123", 1.00 ));
        assertNotNull(storeService.findByID(sid));
        assertNotNull(productService.findByIDAndStore_id(pid, sid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(storeService.findByID(sid));
    }
}