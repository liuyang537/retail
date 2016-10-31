package com.tenx.ms.retail.rest;

import java.util.List;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
public class ProductController {

    private ProductRepository pr;
    private StoreRepository sr;

    public ProductController() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(StoreRepository.class);
        this.sr = ctx1.getBean(StoreRepository.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(ProductRepository.class);
        this.pr = ctx2.getBean(ProductRepository.class);
    }

    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=POST)
    public long addAProduct(@PathVariable("storeId") long store_id, @RequestBody Product product, HttpServletResponse response) {
        String name = product.getName();
        String description = product.getDescription();
        Double price = product.getPrice();
        String sku = product.getSku();

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return -1;
        }

        Product p = pr.findAProduct(store_id, name);
        if(p != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return -2;
        }

        p = pr.findSku(store_id, sku);
        if(p != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return -3;
        }
        System.out.print("");
        if(name.length() == 0 || description.length() > 255 || product.invalidSku() || product.invalidPrice() ){
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return -4;
        }

        return pr.addProduct(name, description, price, sku, store_id);
    }


    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=GET)
    public List<Product> listProducts(@PathVariable("storeId") long store_id, HttpServletResponse response){

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        List<Product> products = pr.listAllProducts(store_id);
        if(products.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
        }

        JSONObject responseDetailsJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(Product p : products){
            JSONObject productDetailsJson = new JSONObject();
            productDetailsJson.put("product_id", p.getProduct_id());
            productDetailsJson.put("store_id", p.getStore_id());
            productDetailsJson.put("name", p.getName());
            productDetailsJson.put("description", p.getDescription());
            productDetailsJson.put("price", p.getPrice());
            productDetailsJson.put("sku", p.getSku());
            jsonArray.add(productDetailsJson);
        }
        responseDetailsJson.put("products", jsonArray);
        return products;
    }

    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=GET, params = "productId")
    public Product getAProduct(@PathVariable("storeId") long store_id,
                               @RequestParam(value="productId", defaultValue="0") long product_id, HttpServletResponse response){

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Product p = pr.findAProduct(store_id, product_id);
        if(p == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return p;
        }

        JSONObject productDetailsJson = new JSONObject();
        productDetailsJson.put("product_id", p.getProduct_id());
        productDetailsJson.put("store_id", p.getStore_id());
        productDetailsJson.put("name", p.getName());
        productDetailsJson.put("description", p.getDescription());
        productDetailsJson.put("price", p.getPrice());
        productDetailsJson.put("sku", p.getSku());
        return p;
    }

    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=GET, params = "name")
    public Product getAProduct(@PathVariable("storeId") long store_id,
                               @RequestParam(value="name", defaultValue="") String name, HttpServletResponse response){

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Product p = pr.findAProduct(store_id, name);
        if(p == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return p;
        }

        JSONObject productDetailsJson = new JSONObject();
        productDetailsJson.put("product_id", p.getProduct_id());
        productDetailsJson.put("store_id", p.getStore_id());
        productDetailsJson.put("name", p.getName());
        productDetailsJson.put("description", p.getDescription());
        productDetailsJson.put("price", p.getPrice());
        productDetailsJson.put("sku", p.getSku());
        return p;
    }

    @RequestMapping(method = DELETE, path = "/retail/v1/products/{storeId}/", params="productId")
    public void delete(@PathVariable("storeId") long store_id, @RequestParam(value="productId") Long product_id, HttpServletResponse response) {

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Product p = pr.findAProduct(store_id, product_id);
        if(p == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        pr.deleteProduct(store_id, product_id);
    }

}
