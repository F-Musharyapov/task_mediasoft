package dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO класс для ответа получения данных продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductGetByIdResponse {

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
     * Категория продукта. Доступные значения: VEGETABLES, FRUITS
     */
    private String category;

    /**
     * Дополнительная информация о продукте
     */
    private String dictionary;

    /**
     * Цена продукта
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    /**
     * Количество продукта доступное для заказа
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal qty;

    /**
     * Дата и время добавления продукта (TIMESTAMP WITH TIME ZONE, NOT NULL, DEFAULT now()).
     */
    private String insertedAt;

    /**
     * Дата и время последнего изменения количества продукта (TIMESTAMP WITH TIME ZONE)
     */
    private String last_qty_changed;

    /**
     * Валюта цены
     */
    private String currency;
}