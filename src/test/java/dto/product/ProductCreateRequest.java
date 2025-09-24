package dto.product;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO класс для запроса создания продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    /**
     * Название продукта
     */
    private String name;

    /**
     * Артикул продукта. Должен быть уникальным (формат UUID)
     */
    private String article;

    /**
     * Категория продукта. Доступные значения: VEGETABLES, FRUITS
     */
    private String category;

    /**
     * Дополнительная информация о продукте
     */
    private String dictionary;

    /**
     * Цена продукта. Должна быть больше 0
     */
    private BigDecimal price;

    /**
     * Количество продукта. Должно быть больше 0
     */
    private int qty;
}