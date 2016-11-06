package com.tenx.ms.retail;

import com.tenx.ms.retail.service.StoreService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenx.ms.retail.domain.Store;
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
import java.util.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class StoreControllerTest {

    @Autowired
    private StoreService storeService;

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testAddStoreApi1() throws JsonProcessingException {//Reference case

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "store1");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/stores", httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 200);
        String sid = apiResponse.getBody();
        assertNotNull(sid);

        storeService.delete(Long.parseLong(sid));
    }

    @Test
    public void testAddStoreApi2() throws JsonProcessingException {//duplicated store name

        long sid = storeService.save(new Store("store1"));

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "store1");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/stores", httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 409);

        storeService.delete(sid);
    }

    @Test
    public void testAddStoreApi3() throws JsonProcessingException {//store name is null

        //Building the Request body data
        JSONObject request = new JSONObject();
        request.put("name", "");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Creating http entity object with request body and headers
        HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), requestHeaders);

        //Invoking the API
        ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:8080/retail/v1/stores", httpEntity, String.class);
        HttpStatus status = apiResponse.getStatusCode();
        assertEquals(status.value(), 412);

    }

    @Test
    public void testListAllStoresApi1(){//Reference case

        Long sid1 = storeService.save(new Store("store1"));
        Long sid2 = storeService.save(new Store("store2"));
        Map<Long, String> stores = new HashMap<>();
        stores.put(sid1, "store1");
        stores.put(sid2, "store2");

        //Invoke the API
        List<Store> apiResponse = restTemplate.getForObject("http://localhost:8080/retail/v1/stores", List.class);

        assertNotNull(apiResponse);

        assertTrue(apiResponse.size() == 2);

//        for(Store s : allStores){
//            assertEquals(stores.get(s.getStore_id()), s.getName());
//        }
        storeService.delete(sid1);
        storeService.delete(sid2);
    }

    @Test
    public void testListAllStoresApi2(){//Reference case

        //Invoke the API
        List<Store> apiResponse = restTemplate.getForObject("http://localhost:8080/retail/v1/stores", List.class);
        assertNull(apiResponse);
    }

    @Test
    public void testFindAStore1(){//no store found
        long sid = storeService.save(new Store("store1"));

        Store apiResponse1 = restTemplate.getForObject("http://localhost:8080/retail/v1/stores?storeId=" + sid, Store.class);
        assertNotNull(apiResponse1);
        assertEquals(apiResponse1.getStore_id(), sid);
        assertEquals(apiResponse1.getName(), "store1");

        Store apiResponse2 = restTemplate.getForObject("http://localhost:8080/retail/v1/stores?name=store1" , Store.class);
        assertNotNull(apiResponse2);
        assertEquals(apiResponse2.getStore_id(), sid);
        assertEquals(apiResponse2.getName(), "store1");

        storeService.delete(sid);

    }

    @Test
    public void testFindAStore2(){//no store found
        long sid = storeService.save(new Store("store1"));
        long originalSid = sid;

        Store s = storeService.findByID(sid);
        while(s != null){
            sid = (long)(Math.random() * 100 + 1);
            s = storeService.findByID(sid);
        }

        ResponseEntity<String> apiResponse1 = restTemplate.getForEntity("http://localhost:8080/retail/v1/stores?storeId=" + sid, String.class);
        HttpStatus status1 = apiResponse1.getStatusCode();
        assertEquals(status1.value(), 404);

        ResponseEntity<String> apiResponse2 = restTemplate.getForEntity("http://localhost:8080/retail/v1/stores?name=store2", String.class);
        HttpStatus status2 = apiResponse2.getStatusCode();
        assertEquals(status2.value(), 404);

        storeService.delete(originalSid);

    }

    @Test
    public void testDeleteStore(){

        long sid = storeService.save(new Store("store1"));
        assertNotNull(storeService.findByID(sid));

        restTemplate.delete("http://localhost:8080/retail/v1/stores?storeId=" + sid);
        assertNull(storeService.findByID(sid));
    }
}
