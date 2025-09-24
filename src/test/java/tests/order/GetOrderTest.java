package tests.order;

import database.OrderSqlSteps;
import database.ProductSqlSteps;
import dto.order.OrderCreateRequest;
import dto.order.OrderCreateResponse;
import dto.order.OrderGetResponse;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static helpers.AssertsHelper.assertCreateOrderFieldsEqual;
import static helpers.AssertsHelper.assertGetOrderFieldsEqual;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс для проверки функциональности получения информации о заказе.
 * Содержит тесты для проверки получения данных заказа через API и корректности данных в ответе.
 */
@Epic("Order Management")
@Feature("Order Retrieval")
public class GetOrderTest extends BaseTest {

    /**
     * ID клиента для тестов
     */
    public int customerID;

    /**
     * Запрос на создание первого продукта
     */
    private ProductCreateRequest firstProductCreateRequest;

    /**
     * Запрос на создание второго продукта
     */
    private ProductCreateRequest secondProductCreateRequest;

    /**
     * Ответ на создание первого продукта
     */
    private ProductCreateResponse firstProductCreateResponse;

    /**
     * Ответ на создание второго продукта
     */
    private ProductCreateResponse secondProductCreateResponse;

    /**
     * Запрос на создание заказа
     */
    private OrderCreateRequest orderCreateRequest;

    /**
     * Ответ на создание заказа
     */
    private OrderCreateResponse orderCreateResponse;

    /**
     * Ответ на запрос получения информации о заказе
     */
    private OrderGetResponse orderGetResponse;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестового клиента, продукты и заказ для использования в тестах получения заказа.
     */
    @BeforeEach
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
    }

    /**
     * Тест проверяет функциональность получения информации о заказе.
     * Выполняет запрос на получение данных заказа и проверяет корректность ответа.
     */
    @Test
    @Description("Тест получения информации о заказе: проверка корректности данных в ответе")
    public void orderGetTest() {
        orderGetResponse = given()
                .when()
                .header(CUSTOMER_ID, customerID)
                .get(config.getOrderByIdEndpoint() + orderCreateResponse.getId())
                .then()
                .statusCode(STATUS_CODE_OK)
                .extract().as(OrderGetResponse.class);

        assertGetOrderFieldsEqual(new OrderSqlSteps().getOrderBDModel((orderCreateResponse.getId())), orderGetResponse);
    }

    /**
     * Метод очистки, выполняемый после каждого теста.
     * Удаляет созданный тестовый продукт из базы данных.
     * Обрабатывает возможные исключения при удалении.
     */
    @AfterEach
    @Description("Удаление Product после завершения тестов")
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
