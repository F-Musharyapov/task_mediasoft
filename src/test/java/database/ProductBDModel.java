package database;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * DTO класс для получения данных продукта из БД
 */
@Data
@Builder
@EqualsAndHashCode(exclude = {"id"})
public class ProductBDModel {

    /**
     * Уникальный идентификатор продукта (UUID, PRIMARY KEY, генерируется автоматически).
     */
    private String id;

    /**
     * Название продукта
     */
    private String name;

    /**
     * Артикул продукта. Должен быть уникальным (формат UUID)
     */
    private String article;

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
    private BigDecimal price;

    /**
     * Количество продукта
     */
    private BigDecimal qty;

    /**
     * Дата и время добавления продукта (TIMESTAMP WITH TIME ZONE, NOT NULL, DEFAULT now()).
     */
    private String inserted_at;

    /**
     * Дата и время последнего изменения количества продукта (TIMESTAMP WITH TIME ZONE)
     */
    private String last_qty_changed;

    /**
     * Доступен ли продукт для заказа (BOOLEAN, NULL DEFAULT false).
     */
    private String is_available;
}