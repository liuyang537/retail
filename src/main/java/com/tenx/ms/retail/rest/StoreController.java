package com.tenx.ms.retail.rest;

import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.service.StoreService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class StoreController {

    @Autowired
    private StoreService storeService;


    @RequestMapping("/")
    public String home() {
        return "Welcome to Retail Services!";
    }

    @RequestMapping(method = POST, path = "/retail/v1/stores")
    public long create(@RequestBody Store store, HttpServletResponse response) {
        Store s = storeService.findByName(store.getName());
        if(s != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return -1;
        }
        else if(store.getName().length() == 0) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return -1;
        }
        else{
            return storeService.save(new Store(store.getName()));
        }
    }

    @RequestMapping(path = "/retail/v1/stores", method=GET)
    public Iterable<Store> list(HttpServletResponse response){
        JSONObject responseDetailsJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Iterable<Store> stores = storeService.listAllStores();
        if(!stores.iterator().hasNext()){
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
        }
        for(Store s : stores){
            JSONObject storeDetailsJson = new JSONObject();
            storeDetailsJson.put("store_id", s.getStore_id());
            storeDetailsJson.put("name", s.getName());
            jsonArray.add(storeDetailsJson);
        }
        responseDetailsJson.put("stores", jsonArray);
        return stores;
    }

    @RequestMapping(path = "/retail/v1/stores", method=GET, params="storeId")
    public Store getAStore(@RequestParam(value = "storeId") Long store_id, HttpServletResponse response){
        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return s;
        }
        JSONObject storeDetailsJson = new JSONObject();
        storeDetailsJson.put("store_id", s.getStore_id());
        storeDetailsJson.put("name", s.getName());
        return s;
    }

    @RequestMapping(path = "/retail/v1/stores", method=GET, params="name")
    public Store getAStore(@RequestParam(value = "name") String name, HttpServletResponse response){
        Store s = storeService.findByName(name);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return s;
        }
        JSONObject storeDetailsJson = new JSONObject();
        storeDetailsJson.put("store_id", s.getStore_id());
        storeDetailsJson.put("name", s.getName());
        return s;
    }

    @RequestMapping(method = DELETE, path = "/retail/v1/stores", params="storeId")
    public void delete(@RequestParam(value = "storeId") Long store_id, HttpServletResponse response) {
        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        storeService.delete(store_id);
    }
}
