package com.tenx.ms.retail.rest;


import java.util.List;
import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.service.ProductService;
import com.tenx.ms.retail.service.StoreService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=POST)
    public long addAProduct(@PathVariable("storeId") long store_id, @RequestBody Product product, HttpServletResponse response) {
        String name = product.getName();
        String description = product.getDescription();
        Double price = product.getPrice();
        String sku = product.getSku();

        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return -1;
        }

        Product p = productService.findByNameAndStore_id(name, store_id);
        if(p != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return -2;
        }

        p = productService.findBySkuAndStore_id(sku, store_id);
        if(p != null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return -2;
        }

        if(name.length() == 0 || description.length() > 255 || product.invalidSku() || product.invalidPrice() ){
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return -3;
        }

        return productService.save(new Product(store_id, name, description, sku, price));
    }

    @RequestMapping(path = "/retail/v1/products/{storeId}/", method=GET)
    public List<Product> listProducts(@PathVariable("storeId") long store_id, HttpServletResponse response){

        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        List<Product> products = productService.list(store_id);
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

        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Product p = productService.findByIDAndStore_id(product_id,store_id);
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

        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Product p = productService.findByNameAndStore_id(name, store_id);
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

        Store s = storeService.findByID(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Product p = productService.findByIDAndStore_id(product_id, store_id);
        if(p == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        productService.delete(product_id, store_id);
    }

}
