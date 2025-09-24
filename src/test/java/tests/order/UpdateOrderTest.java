package tests.order;

import database.OrderSqlSteps;
import database.ProductSqlSteps;
import dto.order.OrderCreateRequest;
import dto.order.OrderCreateResponse;
import dto.order.OrderUpdateRequest;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static helpers.AssertsHelper.*;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс для проверки функциональности обновления заказов.
 * Содержит тесты для проверки создания заказа через API и корректности данных в БД.
 */
@Epic("Order Management")
@Feature("Order Update")
public class UpdateOrderTest extends BaseTest {

    /**
     * ID клиента для тестов
     */
    public int customerID;

    /**
     * Запрос на создание первого продукта
     */
    private ProductCreateRequest firstProductCreateRequest;

    /**
     * Ответ на создание первого продукта
     */
    private ProductCreateResponse firstProductCreateResponse;

    /**
     * Запрос на создание второго продукта
     */
    private ProductCreateRequest secondProductCreateRequest;

    /**
     * Ответ на создание второго продукта
     */
    private ProductCreateResponse secondProductCreateResponse;

    /**
     * Запрос на создание третьего продукта
     */
    private ProductCreateRequest thirdProductCreateRequest;

    /**
     * Ответ на создание третьего продукта
     */
    private ProductCreateResponse thirdProductCreateResponse;

    /**
     * Запрос на создание заказа
     */
    private OrderCreateRequest orderCreateRequest;

    /**
     * Ответ на создание заказа
     */
    private OrderCreateResponse orderCreateResponse;

    /**
     * Запрос на обновление заказа
     */
    private OrderUpdateRequest orderUpdateRequest;


    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестового клиента, продукты и заказ для использования в тестах обновления заказов.
     */
    @BeforeEach
    @Description("Подготовка тестовых данных: создание клиента, продуктов и заказа")
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
     * Тест проверяет функциональность обновления заказа.
     * Создает дополнительный продукт, обновляет заказ, добавляя новый продукт,
     * и проверяет корректность обновления данных в базе данных.
     */
    @Test
    @Description("Тест обновления заказа: добавление нового продукта и проверка корректности данных")
    public void updateOrderTest() {
        thirdProductCreateRequest = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        thirdProductCreateResponse = given()
                .spec(requestSpecification)
                .body(thirdProductCreateRequest)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);

        List<OrderUpdateRequest.Products> productsList = new ArrayList<>();
        productsList.add(OrderUpdateRequest.Products.builder()
                .id(firstProductCreateResponse.getId())
                .qty(generateRandomQtyForOrderCreateTest(firstProductCreateRequest.getQty()))
                .build());

        productsList.add(OrderUpdateRequest.Products.builder()
                .id(secondProductCreateResponse.getId())
                .qty(generateRandomQtyForOrderCreateTest(secondProductCreateRequest.getQty()))
                .build());

        productsList.add(OrderUpdateRequest.Products.builder()
                .id(thirdProductCreateResponse.getId())
                .qty(generateRandomQtyForOrderCreateTest(thirdProductCreateRequest.getQty()))
                .build());

        orderUpdateRequest = OrderUpdateRequest.builder()
                .products(productsList)
                .build();

        given()
                .spec(requestSpecification)
                .body(orderUpdateRequest)
                .when()
                .header(CUSTOMER_ID, customerID)
                .patch(config.updateOrderEndpoint() + orderCreateResponse.getId())
                .then()
                .statusCode(STATUS_CODE_UPDATE);

        assertUpdateOrderFieldsEqual(new OrderSqlSteps().getOrderBDModel((orderCreateResponse.getId())), orderUpdateRequest);

        assertEquals(firstProductCreateRequest.getQty() - new OrderSqlSteps().getQtyProductOrder(orderCreateResponse.getId(), firstProductCreateResponse.getId()),
                new ProductSqlSteps().getProductBDModel(firstProductCreateResponse.getId()).getQty().intValue(),
                "Подсчет количества продуктов после создания заказа некорректен");

        assertEquals(secondProductCreateRequest.getQty() - new OrderSqlSteps().getQtyProductOrder(orderCreateResponse.getId(), secondProductCreateResponse.getId()),
                new ProductSqlSteps().getProductBDModel(secondProductCreateResponse.getId()).getQty().intValue(),
                "Подсчет количества продуктов после создания заказа некорректен");

        assertEquals(thirdProductCreateRequest.getQty() - new OrderSqlSteps().getQtyProductOrder(orderCreateResponse.getId(), thirdProductCreateResponse.getId()),
                new ProductSqlSteps().getProductBDModel(thirdProductCreateResponse.getId()).getQty().intValue(),
                "Подсчет количества продуктов после создания заказа некорректен");
    }

    /**
     * Метод очистки, выполняемый после каждого теста.
     * Удаляет созданные тестовые данные: заказ, клиента и продукты из базы данных.
     */
    @AfterEach
    @Description("Удаление order, product и customer после завершения тестов")
    public void deleteOrderInDataBase() {
        if (orderCreateResponse != null) {
            try {
                new OrderSqlSteps().deleteOrder((orderCreateResponse.getId()));
                new OrderSqlSteps().deleteCustomer(customerID);
                new ProductSqlSteps().deleteProduct(firstProductCreateResponse.getId());
                new ProductSqlSteps().deleteProduct(secondProductCreateResponse.getId());
                new ProductSqlSteps().deleteProduct(thirdProductCreateResponse.getId());
            } catch (Exception e) {
                System.err.println("Ошибка при удалении заказа: " + e.getMessage());
            }
        }
    }
}