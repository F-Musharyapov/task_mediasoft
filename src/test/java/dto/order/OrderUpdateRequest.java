package dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO класс для запроса обновления заказа.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    /**
     * Состав заказа
     */
    private List<OrderUpdateRequest.Products> products;

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
         * Количество продукта
         */
        private int qty;
    }
}