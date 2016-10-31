package com.tenx.ms.retail.rest;

import com.tenx.ms.retail.entity.Stock;
import com.tenx.ms.retail.repository.StockRepository;
import net.minidev.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class StockController {

    private StockRepository tr;

    public StockController() {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(StockRepository.class);
        this.tr = ctx.getBean(StockRepository.class);
    }

    @RequestMapping(path = "/retail/v1/stock/{storeId}/{productId}", method=POST)
    public void updateStock(@PathVariable("storeId") long store_id,
                            @PathVariable("productId") long product_id,
                            @RequestBody Stock stock, HttpServletResponse response){

        if(stock.getCount() < 0 || tr.updateQuantity(store_id, product_id, stock.getCount(), true) != 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return;
    }

    @RequestMapping(path = "/retail/v1/stock/{storeId}/{productId}", method=GET)
    public Stock gerStock(@PathVariable("storeId") long store_id,
                              @PathVariable("productId") long product_id, HttpServletResponse response){

        Stock s = tr.getQuantity(store_id, product_id);
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