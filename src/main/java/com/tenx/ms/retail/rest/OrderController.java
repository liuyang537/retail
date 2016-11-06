package com.tenx.ms.retail.rest;

import java.util.List;
import com.tenx.ms.retail.domain.*;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import com.tenx.ms.retail.service.OrderService;
import com.tenx.ms.retail.service.StoreService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    @RequestMapping(path = "/retail/v1/orders/{storeId}/", method=POST)
    public OrderStatus create(@PathVariable("storeId") long store_id,
                              @RequestBody Order order, HttpServletResponse response){
        LocalDate order_date = ( order.getOrder_date() == null )? LocalDate.now():order.getOrder_date();
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        String first_name = order.getFirst_name();
        String last_name = order.getLast_name();
        String email = order.getEmail();
        String phone = order.getPhone();

        Store store = storeService.findByID(store_id);
        if(store == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        if(first_name.length() == 0 || order.invalidEmail() || order.invalidPhone()){
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }

        OrderStatus orderStatus = orderService.create(store_id, order_date, first_name, last_name,  email, phone, orderedProducts);
        JSONObject orderStatusDetailsJson = new JSONObject();
        orderStatusDetailsJson.put("order_id", orderStatus.getOrder_id());
        orderStatusDetailsJson.put("staus", orderStatus.getStatus());
        orderStatusDetailsJson.put("details", orderStatus.getDetails());
        return orderStatus;
    }
}
