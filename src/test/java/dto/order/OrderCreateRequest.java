package dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO класс для запроса создания заказа.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    /**
     * Адрес доставки
     */
    private String deliveryAddress;

    /**
     * Состав заказа
     */
    private List<Products> products;

    /**
     * Метод для продуктов внутри products
     */
    @Data
    @Builder
    public static class Products {

        /**
         * id продукта
         */
        private String id;

        /**
         * Количество продуктов
         */
        private int qty;
    }
}