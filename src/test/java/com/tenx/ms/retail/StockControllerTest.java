package com.tenx.ms.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StockRepository;
import com.tenx.ms.retail.repository.StoreRepository;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class StockControllerTest {
    //Directly invoke the APIs interaction with the DB
    private ProductRepository pr;
    private StoreRepository sr;
    private StockRepository tr;

    public StockControllerTest() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(StoreRepository.class);
        this.sr = ctx1.getBean(StoreRepository.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(ProductRepository.class);
        this.pr = ctx2.getBean(ProductRepository.class);
        ApplicationContext ctx3 = new AnnotationConfigApplicationContext(StockRepository.class);
        this.tr = ctx3.getBean(StockRepository.class);
    }

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testUpdateQuantityApi1() throws JsonProcessingException {//Reference case

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/"+sid+"/"+pid+"/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 200);

        pr.deleteProduct(sid, pid);
        sr.deleteStore(sid);
    }

    @Test
    public void testUpdateQuantityApi2() throws JsonProcessingException {//negative count

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", -5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/"+sid+"/"+pid+"/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        pr.deleteProduct(sid, pid);
        sr.deleteStore(sid);
    }

    @Test
    public void testUpdateQuantityApi3() throws JsonProcessingException {//no such store existed

        long sid = sr.addStore("store1");
        long originalSid = sid;
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);


        Store s = sr.findAStore(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = sr.findAStore(sid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/"+sid+"/"+pid+"/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        pr.deleteProduct(originalSid, pid);
        sr.deleteStore(originalSid);
    }

    @Test
    public void testUpdateQuantityApi4() throws JsonProcessingException {//no such product in current store

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        long originalPid = pid;

        Product p = pr.findAProduct(sid, pid);
        while(p != null){
            pid = (long)(Math.random() * 100 + 1);
            p = pr.findAProduct(sid, pid);
        }

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("count", 5);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);


        //Invoking the API
        String url = "http://localhost:8080/retail/v1/stock/"+sid+"/"+pid+"/";
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(url, httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 400);

        pr.deleteProduct(sid, originalPid);
        sr.deleteStore(sid);
    }

    @Test
    public void testDeleteProductApi(){//test delete product function with stock associate with such product

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        tr.updateQuantity(sid, pid, (long)5, true);
        assertNotNull(pr.findAProduct(sid, pid));
        assertNotNull(tr.getQuantity(sid, pid));

        restTemplate.delete("http://localhost:8080/retail/v1/products/{storeId}/?productId=" + pid, sid);
        assertNull(tr.getQuantity(sid, pid));
        assertNull(pr.findAProduct(sid, pid));

        sr.deleteStore(sid);
    }

    @Test
    public void testDeleteStoreApi(){//test delete product function with stock associate with such product

        long sid = sr.addStore("store1");
        long pid = pr.addProduct("product1", "description1", 1.00, "abc123", sid);
        tr.updateQuantity(sid, pid, (long)5, true);
        assertNotNull(sr.findAStore(sid));
        assertNotNull(pr.findAProduct(sid, pid));
        assertNotNull(tr.getQuantity(sid, pid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(tr.getQuantity(sid, pid));
        assertNull(pr.findAProduct(sid, pid));
        assertNull(sr.findAStore(sid));

    }
}
