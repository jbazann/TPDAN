package com.jbazann.orders.testdata;

import com.jbazann.orders.order.dto.NewDetailDTO;
import com.jbazann.orders.order.dto.NewOrderDTO;
import com.jbazann.orders.order.dto.OrderDTO;
import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.testdata.entities.CustomerMock;
import com.jbazann.orders.testdata.entities.StandardDatasetData;

import java.math.BigDecimal;
import java.util.*;

import static com.jbazann.orders.testdata.generators.StandardDatasetDataGenerator.*;
import static com.jbazann.orders.testdata.entities.StandardDatasetDataBuilder.*;

public enum StandardDataset {

    NEW_VALID(newOrderValid()),
    FREE(newOrderFree()),
    NO_CUSTOMER(newOrderNoCustomer()),
    NO_USER(newOrderNoUser()),
    NO_SITE(newOrderNoSite()),
    NO_DETAIL(newOrderNoDetail()),
    EMPTY_DETAIL(newOrderEmptyDetailList()),
    INVALID_CUSTOMER(newOrderValid()), // Must be mocked correctly.
    PERSISTED_LIST_FROM_CUSTOMER(orderListFromSameCustomer()),
    NOT_FOUND(genericOrder()),
    BUDGET_500(startFrom(genericOrder())
            .setCustomerBudget(new BigDecimal(500))
            .build()
    ),

    ;

    private final StandardDatasetData data;

    StandardDataset(StandardDatasetData data) {
        this.data = data;
    }

    /**
     * An unmodifiable list for initializing test data and verifying correctness.
     * It provides a starting set of "true state" data for reference. For example,
     * a test may compare the result of a filtering operation on this data structure
     * against the results of a database query made by the application logic.
     * <br> <br>
     * Note that datasource mutations performed by a test will not be reflected on this list,
     * it should still have many use cases.
     * <br> <br>
     * The objects in this list may still be modifiable for implementation simplicity, tests should avoid
     * making changes.
     */
    public static final List<Order> DATA = List.copyOf(buildInitData());

    private static List<Order> buildInitData() {
        System.out.println("TPDAN: ### Building initial test data for StandardDataset.");// TODO print
        final List<Order> data = standardOrderList();
        data.addAll(PERSISTED_LIST_FROM_CUSTOMER.data.orders());
        System.out.printf("TPDAN: ### Built %d randomized entries to initialize test database. Seed: %d.%n", data.size(), SEED);// TODO print
        return data;
    }

    private static void resetBudgets() {
        Arrays.stream(StandardDataset.values())
                .map(v -> v.data.customer())
                .filter(Objects::nonNull)
                .filter(c -> c.budget() != null &&
                        c.budget().compareTo(BigDecimal.ZERO) > 0)
                .forEach(CustomerMock::resetBudget);
    }

    /**
     * If this was a serious project this comment would explain exactly what is being reset
     * and what is not. The fact that at the time of writing this it only calls {@link StandardDataset#resetBudgets()}
     * is only due to it <em>currently</em> being the only subset of resettable data.
     * Just call before each test and hope for the best.
     */
    public static void resetData() {
        resetBudgets();
    }

    public CustomerMock getCustomer() {
        if(data.customer() == null) return null;
        return data.customer().copy();
    }

    public NewOrderDTO asNewOrderDTO() {
        final Order o = getOrder();
        return o == null ? null :
                new NewOrderDTO()
                .user(o.user())
                .customer(o.customer())
                .site(o.site())
                .note(o.note())
                .detail(o.detail() == null ? null :
                    o.detail().stream()
                            .map(d -> new NewDetailDTO()
                                    .amount(d.amount())
                                    .unitCost(d.unitCost())
                                    .discount(d.discount())
                                    .product(d.product())
                                    .totalCost(d.totalCost()))
                            .toList()
                );
    }

    public OrderDTO asOrderDTO() {
        Order o = getOrder();
        return o == null ? null : o.toDto();
    }

    public UUID customerId() {
        if( data.customer() != null ) return data.customer().id();
        if( data.order() != null ) return data.order().customer();
        if( data.orders() != null ) return data.orders().getFirst().customer();
        return null;
    }

    public UUID orderId() {
        final Order o = getOrder();
        return o == null ? null : o.id();
    }

    private Order getOrder() {
        Order o = data.order();
        if( o == null && data.orders() != null && !data.orders().isEmpty() )
            o = data.orders().getFirst();
        return o;
    }

}