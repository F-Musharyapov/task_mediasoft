package database;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO класс для получения данных заказа их БД
 */
@Data
@Builder
public class OrderBDModel {

    /**
     * ID заказа
     */
    private String order_id;

    /**
     * ID customer
     */
    private String customer_id;

    /**
     * Статус заказа
     */
    private String status;

    /**
     * Адресс доставки
     */
    private String deliveryAddress;

    /**
     * Состав заказа
     */
    private List<OrderProduct> products;

    /**
     * Метод для продуктов внутри products
     */
    @Data
    @Builder
    public static class OrderProduct {

        /**
         * id продукта
         */
        @JsonProperty("product_id")
        private String id;

        /**
         * Количество продукта
         */
        private int qty;

        /**
         * Цена продукта
         */
        private BigDecimal price;

        /**
         * Название продукта
         */
        private String name;
    }
}