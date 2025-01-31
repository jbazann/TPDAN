package dev.jbazann.skwidl.products.product.dto;

import dev.jbazann.skwidl.products.product.Product;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public class ProductDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int currentStock;
    private int minimumStock;
    private UUID category;

    public Product toEntity() {
        return new Product(id, name, description, price, currentStock, minimumStock, category);
    }

}
