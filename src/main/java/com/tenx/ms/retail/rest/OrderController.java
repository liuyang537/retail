package com.tenx.ms.retail.rest;

import java.util.List;
import com.tenx.ms.retail.entity.OrderStatus;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.entity.Order;
import com.tenx.ms.retail.entity.OrderedProduct;
import com.tenx.ms.retail.entity.Store;
import net.minidev.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class OrderController {

    private OrderRepository or;
    private StoreRepository sr;

    public OrderController() {
            ApplicationContext ctx1 = new AnnotationConfigApplicationContext(StoreRepository.class);
            this.sr = ctx1.getBean(StoreRepository.class);
            ApplicationContext ctx2 = new AnnotationConfigApplicationContext(OrderRepository.class);
            this.or = ctx2.getBean(OrderRepository.class);
    }

    @RequestMapping(path = "/retail/v1/orders/{storeId}/", method=POST)
    public OrderStatus create(@PathVariable("storeId") long store_id,
                              @RequestBody Order order, HttpServletResponse response){
        LocalDate order_date = ( order.getOrder_date() == null )? LocalDate.now():order.getOrder_date();
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        String first_name = order.getFirst_name();
        String last_name = order.getLast_name();
        String email = order.getEmail();
        String phone = order.getPhone();

        Store s = sr.findAStore(store_id);
        if(s == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        if(first_name.length() == 0 || order.invalidEmail() || order.invalidPhone()){
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }

        OrderStatus orderStatus = or.createOrder (store_id, order_date, orderedProducts, first_name, last_name,  email, phone);
        JSONObject orderStatusDetailsJson = new JSONObject();
        orderStatusDetailsJson.put("order_id", orderStatus.getOrder_id());
        orderStatusDetailsJson.put("staus", orderStatus.getStatus());
        orderStatusDetailsJson.put("details", orderStatus.getDetails());
        return orderStatus;
    }
}
