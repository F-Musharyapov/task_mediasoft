package helpers;

import database.OrderBDModel;
import database.ProductBDModel;
import dto.order.OrderCreateRequest;
import dto.order.OrderCreateResponse;
import dto.order.OrderGetResponse;
import dto.order.OrderUpdateRequest;
import dto.product.ProductCreateResponse;
import dto.product.ProductGetByIdResponse;
import dto.product.ProductUpdateResponse;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static helpers.DataConverter.parseApiDateToUtcOffset;
import static helpers.DataConverter.parseDbDateToUtcOffset;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Набор Asserts
 */
public class AssertsHelper extends Assertions {

    /**
     * Сравнение данных для класса CreateProductTest (Внимание! Поле dictionary не возвращается в API ответе, соответственно не проверяется)
     *
     * @param expected ожидаемые данные
     * @param actual актуальные данные
     */
    public static void assertCreateProductFieldsEqual(ProductBDModel expected, ProductCreateResponse actual) {
        assertCheckField("name", expected.getName(), actual.getName());
        assertCheckField("article", expected.getArticle(), actual.getArticle());
        assertCheckField("id", expected.getId(), actual.getId());
        assertCheckField("category", expected.getCategory(), actual.getCategory());
        assertCheckField("price", expected.getPrice(), actual.getPrice().setScale(2, RoundingMode.HALF_UP));
        assertCheckField("qty", expected.getQty(), actual.getQty().setScale(2, RoundingMode.HALF_UP));
        assertCheckField("insertedAt", parseDbDateToUtcOffset(expected.getInserted_at()), parseApiDateToUtcOffset(actual.getInsertedAt()));
        assertCheckField("last_qty_changed", parseDbDateToUtcOffset(expected.getLast_qty_changed()), parseApiDateToUtcOffset(actual.getLast_qty_changed()));
    }

    /**
     * Сравнение данных для класса GetProductByIdTest (Внимание! Поле dictionary не возвращается в API ответе, соответственно не проверяется)
     *
     * @param expected ожидаемые данные
     * @param actual актуальные данные
     */
    public static void assertGetProductByIdFieldsEqual(ProductBDModel expected, ProductGetByIdResponse actual) {
        assertCheckField("name", expected.getName(), actual.getName());
        assertCheckField("article", expected.getArticle(), actual.getArticle());
        assertCheckField("id", expected.getId(), actual.getId());
        assertCheckField("category", expected.getCategory(), actual.getCategory());
        assertCheckField("price", expected.getPrice(), actual.getPrice().setScale(2, RoundingMode.HALF_UP));
        assertCheckField("qty", expected.getQty(), actual.getQty().setScale(2, RoundingMode.HALF_UP));
        assertCheckField("insertedAt", parseDbDateToUtcOffset(expected.getInserted_at()), parseApiDateToUtcOffset(actual.getInsertedAt()));
        assertCheckField("last_qty_changed", parseDbDateToUtcOffset(expected.getLast_qty_changed()), parseApiDateToUtcOffset(actual.getLast_qty_changed()));
    }

    /**
     * Сравнение данных для класса UpdateProductTest (Внимание! Поле dictionary не возвращается в API ответе, соответственно не проверяется)
     *
     * @param expected ожидаемые данные
     * @param actual актуальные данные
     */
    public static void assertUpdateProductFieldsEqual(ProductBDModel expected, ProductUpdateResponse actual) {
        assertCheckField("name", expected.getName(), actual.getName());
        assertCheckField("article", expected.getArticle(), actual.getArticle());
        assertCheckField("id", expected.getId(), actual.getId());
        assertCheckField("category", expected.getCategory(), actual.getCategory());
        assertCheckField("price", expected.getPrice(), actual.getPrice().setScale(2, RoundingMode.HALF_UP));
        assertCheckField("qty", expected.getQty(), actual.getQty().setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Метод проверки совпадения проверяемого поля
     *
     * @param fieldName проверяемое поле
     * @param expectedValue ожидаемые данные
     * @param actualValue актуальные данные
     */
    private static void assertCheckField(String fieldName, Object expectedValue, Object actualValue) {
        if (!Objects.equals(expectedValue, actualValue)) {
            throw new AssertionError(fieldName + " не совпадает: expected= " + expectedValue + ", actual= " + actualValue);
        }
    }

    /**
     * Проверка наличия product в БД
     *
     * @param bdObject ответ наличия product в БД
     */
    public static void assertProductDeletedBD(Object bdObject) {
        assertThat(bdObject)
                .withFailMessage("Product не удалился из БД")
                .isNull();
    }

    /**
     * Сравнение данных для класса CreateOrderTest
     * @param expected данные заказа из бд
     * @param actual данные тела запроса создания заказа
     * @param orderId из ответа запроса создания заказа
     * @param customerID идентификатор пользователя при его создании
     */
    public static void assertCreateOrderFieldsEqual(OrderBDModel expected, OrderCreateRequest actual, OrderCreateResponse orderId, int customerID) {
        assertCheckField("order_id", expected.getOrder_id(), orderId.getId());
        assertCheckField("customer_id", expected.getCustomer_id(), String.valueOf(customerID));
        assertCheckField("deliveryAddress", expected.getDeliveryAddress(), actual.getDeliveryAddress());
        //assertCheckField("products", expected.getProducts(), actual.getProducts());
        // Сравнение продуктов
        assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Количество продуктов не совпадает");

        List<OrderBDModel.OrderProduct> sortedExpectedProducts = expected.getProducts().stream()
                .sorted(Comparator.comparing(OrderBDModel.OrderProduct::getId))
                .collect(Collectors.toList());

        List<OrderCreateRequest.Products> sortedActualProducts = actual.getProducts().stream()
                .sorted(Comparator.comparing(OrderCreateRequest.Products::getId))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedExpectedProducts.size(); i++) {
            OrderBDModel.OrderProduct expectedProduct = sortedExpectedProducts.get(i);
            OrderCreateRequest.Products actualProduct = sortedActualProducts.get(i);

            assertCheckField("ID продукта", expectedProduct.getId(), actualProduct.getId());
            assertCheckField("Количество продукта", expectedProduct.getQty(), actualProduct.getQty());
            //assertEquals(expectedProduct.getId(), actualProduct.getId(), "ID продукта " + i + " не совпадает");
            //assertEquals(expectedProduct.getQty(), actualProduct.getQty(), "Количество продукта " + i + " не совпадает");
        }
    }

    /**
     * Метод проверки удаления заказа из БД
     * @param bdObject объект проверки
     */
    public static void assertOrderDeletedBD(Object bdObject) {
        assertThat(bdObject)
                .withFailMessage("Order не удалился из БД")
                .isNull();
    }

    /**
     * Метод сравнения данных заказа из ответа GET-запроса и данных в бд
     * @param expected данные заказа из бд
     * @param actual данные заказа из запроса
     */
    public static void assertGetOrderFieldsEqual(OrderBDModel expected, OrderGetResponse actual) {
        assertCheckField("order_id", expected.getOrder_id(), actual.getOrderId());
        assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Количество продуктов не совпадает");

        BigDecimal totalPriceOrderBD = expected.getProducts().stream()
                .map(OrderBDModel.OrderProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertCheckField("totalPrice", totalPriceOrderBD, actual.getTotalPrice());

        List<OrderBDModel.OrderProduct> sortedExpectedProducts = expected.getProducts().stream()
                .sorted(Comparator.comparing(OrderBDModel.OrderProduct::getId))
                .collect(Collectors.toList());

        List<OrderGetResponse.Products> sortedActualProducts = actual.getProducts().stream()
                .sorted(Comparator.comparing(OrderGetResponse.Products::getId))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedExpectedProducts.size(); i++) {
            OrderBDModel.OrderProduct expectedProduct = sortedExpectedProducts.get(i);
            OrderGetResponse.Products actualProduct = sortedActualProducts.get(i);

            assertCheckField("ID продукта", expectedProduct.getId(), actualProduct.getId());
            assertCheckField("Название продукта", expectedProduct.getName(), actualProduct.getName());
            assertCheckField("Количество продукта", expectedProduct.getQty(), actualProduct.getQty());
            assertCheckField("Цена продукта", expectedProduct.getPrice(), actualProduct.getPrice());
        }
    }

    /**
     * Метод проверки данных заказа после update-запроса api
     * @param expected данные заказа из бд
     * @param actual данные заказа из запроса на изменение заказа
     */
    public static void assertUpdateOrderFieldsEqual(OrderBDModel expected, OrderUpdateRequest actual) {
        assertEquals(expected.getProducts().size(), actual.getProducts().size(), "Количество продуктов не совпадает");

        List<OrderBDModel.OrderProduct> sortedExpectedProducts = expected.getProducts().stream()
                .sorted(Comparator.comparing(OrderBDModel.OrderProduct::getId))
                .collect(Collectors.toList());
        List<OrderUpdateRequest.Products> sortedActualProducts = actual.getProducts().stream()
                .sorted(Comparator.comparing(OrderUpdateRequest.Products::getId))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedExpectedProducts.size(); i++) {
            OrderBDModel.OrderProduct expectedProduct = sortedExpectedProducts.get(i);
            OrderUpdateRequest.Products actualProduct = sortedActualProducts.get(i);

            assertCheckField("ID продукта", expectedProduct.getId(), actualProduct.getId());
            assertCheckField("Количество продукта", expectedProduct.getQty(), actualProduct.getQty());
        }
    }
}