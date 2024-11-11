package com.jbazann.orders.order.dto;

import com.jbazann.orders.order.entities.Detail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class NewDetailDTO {

    private UUID product;
    private Integer amount;
    private BigDecimal discount;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public Detail toEntity() {
        return new Detail()
                .product(product)
                .amount(amount)
                .discount(discount)
                .unitCost(unitCost)
                .totalCost(Detail.calculateTotalCost(amount, discount, unitCost));
    }

}