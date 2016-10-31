package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
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
import java.util.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ProductControllerTest {

    //Directly invoke the APIs interacting with the DB
    private ProductRepository pr;
    private StoreRepository sr;

    public ProductControllerTest() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(StoreRepository.class);
        this.sr = ctx1.getBean(StoreRepository.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(ProductRepository.class);
        this.pr = ctx2.getBean(ProductRepository.class);
    }

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testAddProductApi() throws JsonProcessingException {//Reference case

        long sid = sr.addStore("store1");

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

        pr.deleteProduct(sid, apiResponse);
        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi1() throws JsonProcessingException {//Reference case

        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi2() throws JsonProcessingException {//no such store exist

        long sid = sr.addStore("store1");
        long originalSid = sid;

        Store s = sr.findAStore(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = sr.findAStore(sid);
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

        sr.deleteStore(originalSid);
    }

    @Test
    public void testAddProductApi3() throws JsonProcessingException {//duplicate product name

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);

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

        pr.deleteProduct(sid, pid);
        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi4() throws JsonProcessingException {//duplicate product sku

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "123abc", sid);

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "product2");
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

        pr.deleteProduct(sid, pid);
        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi5() throws JsonProcessingException {//product name is null

        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi6() throws JsonProcessingException {//invalid sku, length is too short

        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi7() throws JsonProcessingException {//invalid sku, length is too long

        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi8() throws JsonProcessingException {//invalid sku, invalid character presented

        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testAddProductApi9() throws JsonProcessingException {//invalid price, too much percision
        long sid = sr.addStore("store1");

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

        sr.deleteStore(sid);
    }

    @Test
    public void testListAllProductsApi1() {//Reference case

        Long sid1 = sr.addStore("store1");
        Long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid1);
        Long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid1);

        //Invoke the API
        List<Product> apiResponse = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/", List.class, sid1);

        assertNotNull(apiResponse);

        assertTrue(apiResponse.size() == 2);

        sr.deleteStore(sid1);
    }

    @Test
    public void testListAllProductsApi2() {//no such store existed

        Long sid = sr.addStore("store1");
        long originalSid = sid;
        Long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        Long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);

        Store s = sr.findAStore(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = sr.findAStore(sid);
        }

        //Invoke the API
        ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/", String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        sr.deleteStore(originalSid);
    }

    @Test
    public void testListAllProductsApi3() {//No products in this store

        Long sid = sr.addStore("store1");

        //Invoke the API
        ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/", String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 204);

        sr.deleteStore(sid);
    }

    @Test
    public void testFindAProduct1(){//Reference case

        long sid1 = sr.addStore("store1");
        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid1);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid1);

        Product apiResponse1 = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid1, Product.class, sid1);
        assertNotNull(apiResponse1);
        assertEquals(apiResponse1.getProduct_id(), pid1);
        assertEquals(apiResponse1.getName(), "product1");

        Product apiResponse2 = restTemplate.getForObject("http://localhost:8080/retail/v1/products/{storeId}/?name=product2", Product.class, sid1);
        assertNotNull(apiResponse2);
        assertEquals(apiResponse2.getProduct_id(), pid2);
        assertEquals(apiResponse2.getName(), "product2");

        sr.deleteStore(sid1);
    }

    @Test
    public void testFindAProduct2() {//No such store existed

        long sid = sr.addStore("store1");
        long originalSid = sid;

        long pid1 = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long pid2 = pr.addProduct("product2", "description2", 2.00, "123abc", sid);

        Store s = sr.findAStore(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = sr.findAStore(sid);
        }

        ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/", String.class, sid);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        sr.deleteStore(originalSid);
    }

    @Test
    public void testFindAProduct3() {

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);

        Product p = pr.findAProduct(sid, pid);
        while(p != null){
            pid = (long)(Math.random() * 100 + 1);
            p = pr.findAProduct(sid, pid);
        }

        ResponseEntity<String> apiResponse1 = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, String.class, sid);
        HttpStatus status1 = apiResponse1.getStatusCode();
        assertEquals(status1.value(), 404);

        ResponseEntity<String> apiResponse2 = restTemplate.getForEntity("http://localhost:8080/retail/v1/products/{storeId}/?name=product2", String.class, sid);
        HttpStatus status2 = apiResponse2.getStatusCode();
        assertEquals(status2.value(), 404);

        sr.deleteStore(sid);
    }

    @Test
    public void testDeleteProductApi1() {

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        assertNotNull(pr.findAProduct(sid, pid));

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(pr.findAProduct(sid, pid));

        sr.deleteStore(sid);
    }

    @Test
    public void testDeleteStoreApi(){//test delete store function with products associated

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        assertNotNull(sr.findAStore(sid));
        assertNotNull(pr.findAProduct(sid, pid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(sr.findAStore(sid));
    }

}
