package tests.order;

import database.OrderSqlSteps;
import database.ProductSqlSteps;
import dto.order.OrderCreateRequest;
import dto.order.OrderCreateResponse;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static helpers.AssertsHelper.assertCreateOrderFieldsEqual;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки функциональности создания заказов.
 * Содержит тесты для проверки создания заказа через API и корректности данных в БД.
 */
@Epic("Order Management")
@Feature("Order Creation")
public class CreateOrderTest extends BaseTest {

    /** ID клиента для тестов */
    public int customerID;

    /** Запрос на создание первого продукта */
    private ProductCreateRequest firstProductCreateRequest;

    /** Запрос на создание второго продукта */
    private ProductCreateRequest secondProductCreateRequest;

    /** Ответ на создание первого продукта */
    private ProductCreateResponse firstProductCreateResponse;

    /** Ответ на создание второго продукта */
    private ProductCreateResponse secondProductCreateResponse;

    /** Запрос на создание заказа */
    private OrderCreateRequest orderCreateRequest;

    /** Ответ на создание заказа */
    private OrderCreateResponse orderCreateResponse;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестового клиента и два продукта для использования в тестах заказов.
     */
    @BeforeEach
    @Description("Подготовка тестовых данных: создание клиента и продуктов")
    public void createCustomerAndProductForOrder() {

        customerID = new OrderSqlSteps().createCustomer(getCustomerRandomNickName(), getCustomerRandomEmail());

        firstProductCreateRequest = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        firstProductCreateResponse = given()
                .spec(requestSpecification)
                .body(firstProductCreateRequest)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);

        secondProductCreateRequest = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        secondProductCreateResponse = given()
                .spec(requestSpecification)
                .body(secondProductCreateRequest)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);
    }

    /**
     * Тест создания заказа и проверки корректности данных.
     * Проверяет:
     * - Создание заказа через API
     * - Корректность статуса заказа в БД
     * - Соответствие данных в API ответе и БД
     * - Корректность обновления количества продуктов после резервирования
     */
    @Test
    @Description("Тест создания заказа и проверки корректности данных в API и БД")
    public void orderCreateTest() {
        List<OrderCreateRequest.Products> productsList = new ArrayList<>();
        productsList.add(OrderCreateRequest.Products.builder()
                .id(firstProductCreateResponse.getId())
                .qty(generateRandomQtyForOrderCreateTest(firstProductCreateRequest.getQty()))
                .build());

        productsList.add(OrderCreateRequest.Products.builder()
                .id(secondProductCreateResponse.getId())
                .qty(generateRandomQtyForOrderCreateTest(secondProductCreateRequest.getQty()))
                .build());

        orderCreateRequest = OrderCreateRequest.builder()
                .deliveryAddress(generateSimpleAddress())
                .products(productsList)
                .build();

        orderCreateResponse = given()
                .spec(requestSpecification)
                .header(CUSTOMER_ID, customerID)
                .body(orderCreateRequest)
                .when()
                .post(config.createOrderEndpoint())
                .then()
                .statusCode(STATUS_CODE_OK)
                .extract().as(OrderCreateResponse.class);

        assertEquals(STATUS_ORDER_CREATED, new OrderSqlSteps().getStatusOrder(orderCreateResponse.getId()), "Статус заказа невалидный");
        assertCreateOrderFieldsEqual(new OrderSqlSteps().getOrderBDModel((orderCreateResponse.getId())), orderCreateRequest, orderCreateResponse, customerID);

        assertEquals(firstProductCreateRequest.getQty() - new OrderSqlSteps().getQtyProductOrder(orderCreateResponse.getId(), firstProductCreateResponse.getId()),
                new ProductSqlSteps().getProductBDModel(firstProductCreateResponse.getId()).getQty().intValue(),
                "Подсчет количества продуктов после создания заказа некорректен");

        assertEquals(secondProductCreateRequest.getQty() -
                        new OrderSqlSteps().getQtyProductOrder(orderCreateResponse.getId(), secondProductCreateResponse.getId()),
                new ProductSqlSteps().getProductBDModel(secondProductCreateResponse.getId()).getQty().intValue(),
                "Подсчет количества продуктов после создания заказа некорректен");
    }

    /**
     * Метод очистки тестовых данных после выполнения каждого теста.
     * Удаляет созданные заказы, продукты и клиентов из базы данных.
     * В случае ошибок при удалении логирует сообщение об ошибке.
     */
    @AfterEach
    @Description("Очистка тестовых данных: удаление заказа, продуктов и клиента")
    public void deleteOrderInDataBase() {
        if (orderCreateResponse != null) {
            try {
                new OrderSqlSteps().deleteOrder((orderCreateResponse.getId()));
                new OrderSqlSteps().deleteCustomer(customerID);
                new ProductSqlSteps().deleteProduct(firstProductCreateResponse.getId());
                new ProductSqlSteps().deleteProduct(secondProductCreateResponse.getId());
            } catch (Exception e) {
                System.err.println("Ошибка при удалении заказа: " + e.getMessage());
            }
        }
    }
}