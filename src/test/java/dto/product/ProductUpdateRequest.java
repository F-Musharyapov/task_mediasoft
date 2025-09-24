package dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO класс для запроса обновления продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    /**
     * Название продукта
     */
    private String name;

    /**
     * Артикул продукта. Должен быть уникальным (формат UUID)
     */
    private String article;

    /**
     * Уникальный идентификатор продукта (UUID, PRIMARY KEY, генерируется автоматически).
     */
    private String id;

    /**
     * Дополнительная информация о продукте
     */
    private String dictionary;

    /**
     * Категория продукта. Доступные значения: VEGETABLES, FRUITS
     */
    private String category;

    /**
     * Цена продукта
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    /**
     * Количество продукта доступное для заказа
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int qty;
}