package com.jbazann.orders.order;

import com.jbazann.orders.order.dto.NewOrderDTO;
import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.dto.StatusUpdateDTO;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;
import com.jbazann.orders.order.exceptions.BadRequestException;
import com.jbazann.orders.order.exceptions.CustomerNotFoundException;
import com.jbazann.orders.order.exceptions.MalformedArgumentException;
import com.jbazann.orders.order.exceptions.OrderNotFoundException;
import com.jbazann.orders.order.services.SyncOrderService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {

    private final SyncOrderService syncOrderService;

    @Autowired
    public OrderController(SyncOrderService syncOrderService) {
        this.syncOrderService = syncOrderService;
    }

    @GetMapping("/order/{id}")
    public OrderDTO getOrder(@NotNull @PathVariable UUID id) {
        return syncOrderService.getOrder(id).toDto();
    }

    @GetMapping("/customer/{id}")
    public List<OrderDTO> getCustomerOrders(@NotNull @PathVariable UUID id) {
        return syncOrderService.getCustomerOrders(id).stream().map(Order::toDto).toList();
    }

    @PostMapping("/order")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO newOrder(@NotNull @RequestBody NewOrderDTO order) {
        return syncOrderService.newOrder(order).toDto();
    }

    @PutMapping("/order/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrder(@PathVariable UUID id,
                                @NotNull @RequestBody StatusUpdateDTO update) {
        if (!syncOrderService.orderExists(id)) throw new OrderNotFoundException("No Order found with id: " + id +'.');
        switch(update.status()) {
            case DELIVERED -> syncOrderService.deliverOrder(id, update);
            case CANCELED -> syncOrderService.cancelOrder(id, update);
            default -> throw new BadRequestException("Status must be one of: " +
                        StatusHistory.Status.DELIVERED + ", " + StatusHistory.Status.CANCELED + '.');
        };
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String malformedArgumentException(MalformedArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Exception exception) { // TODO proper exception handling
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestException(BadRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String customerNotFoundException(CustomerNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String orderNotFoundException(OrderNotFoundException exception) {
        return exception.getMessage();
    }

}
