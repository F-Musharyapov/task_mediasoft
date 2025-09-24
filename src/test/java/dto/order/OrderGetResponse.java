package dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO класс для ответа получения данных заказа.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderGetResponse {

    /**
     * ID заказа
     */
    private String orderId;

    /**
     * Состав заказа
     */
    private List<Products> products;

    /**
     * Метод для продуктов внутри products
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Products {

        /**
         * Название продукта
         */
        private String name;

        /**
         * ID Продукта
         */
        private String id;

        /**
         * Цена продукта
         */
        private BigDecimal price;

        /**
         * Количество продукта
         */
        private int qty;
    }

    /**
     * Цена заказа итого
     */
    private BigDecimal totalPrice;
}