package com.tenx.ms.retail.rest;

import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Stock;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.StockRepository;
import com.tenx.ms.retail.service.ProductService;
import com.tenx.ms.retail.service.StockService;
import com.tenx.ms.retail.service.StoreService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @RequestMapping(path = "/retail/v1/stock/{storeId}/{productId}", method=POST)
    public void updateStock(@PathVariable("storeId") long store_id,
                            @PathVariable("productId") long product_id,
                            @RequestBody Stock stock, HttpServletResponse response){
        Store store = storeService.findByID(store_id);
        if(store == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Product product = productService.findByIDAndStore_id(product_id, store_id);
        if(product == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(stock.getCount() < 0 ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        stockService.set(store, product, stock.getCount());
        return;
    }

    @RequestMapping(path = "/retail/v1/stock/{storeId}/{productId}", method=GET)
    public Stock getStock(@PathVariable("storeId") long store_id,
                          @PathVariable("productId") long product_id, HttpServletResponse response){

        Store store = storeService.findByID(store_id);
        if(store == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Product product = productService.findByIDAndStore_id(product_id, store_id);
        if(product == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Stock s = stockService.get(product, store);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return s;
        }

        JSONObject stockDetailsJson = new JSONObject();
        stockDetailsJson.put("store", s.getStore());
        stockDetailsJson.put("product", s.getProduct());
        stockDetailsJson.put("count", s.getCount());
        return s;
    }
}
