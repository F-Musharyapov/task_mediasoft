package tests.product;

import database.ProductSqlSteps;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Arrays;
import java.util.List;

import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функциональности получения информации о всех продуктах.
 * Содержит тесты для проверки получения данных общего списка продуктов через API и корректности данных в ответе.
 */
@Epic("Product Management")
@Feature("Product Get All")
public class GetAllProductsTest extends BaseTest {

    /**
     * Экземпляр для хранения запроса на создание первого продукта
     */
    private ProductCreateRequest productCreateRequestFirst;

    /**
     * Экземпляр для хранения ответа на создание первого продукта
     */
    private ProductCreateResponse productCreateResponseFirst;

    /**
     * Экземпляр для хранения запроса на создание второго продукта
     */
    private ProductCreateRequest productCreateRequestSecond;

    /**
     * Экземпляр для хранения ответа на создание второго продукта
     */
    private ProductCreateResponse productCreateResponseSecond;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестовые продукты для использования в тестах.
     */
    @BeforeEach
    @Description("Подготовка тестовых данных: создание двух продуктов")
    public void createProducts() {
        productCreateRequestFirst = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        productCreateResponseFirst = given()
                .spec(requestSpecification)
                .body(productCreateRequestFirst)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);

        productCreateRequestSecond = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        productCreateResponseSecond = given()
                .spec(requestSpecification)
                .body(productCreateRequestSecond)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);
    }

    /**
     * Тест проверяет функциональность получения информации о продуктах.
     * Выполняет запрос на получение данных продуктов и проверяет корректность ответа.
     */
    @Test
    @Description("Тест получения информации списка продуктов: проверка корректности данных в ответе")
    public void productGetAllTest() {
        Response response = given()
                .when()
                .get(config.allProductsEndpoint())
                .then()
                .statusCode(STATUS_CODE_OK)
                .extract().response();
        List<String> allProduct = response.jsonPath().getList("id");

        List<String> createdProduct = Arrays.asList(productCreateResponseFirst.getId(), productCreateResponseSecond.getId());
        assertNotNull(allProduct, "Список Product не должен быть null");
        assertTrue(allProduct.containsAll(createdProduct), "Созданные Product не найдены в общем списке тела API запроса");
        assertTrue(new ProductSqlSteps().setSelectIdProductSql().containsAll(createdProduct), "Созданные Product не найдены в базе данных");
    }

    /**
     * Метод очистки, выполняемый после каждого теста.
     * Удаляет созданный тестовый продукт из базы данных.
     * Обрабатывает возможные исключения при удалении.
     */
    @AfterEach
    @Description("Удаление Product после завершения тестов")
    public void productAfterCreationDelete() {
        deleteProductInDataBase(productCreateResponseFirst.getId());
        deleteProductInDataBase(productCreateResponseSecond.getId());
    }

    /**
     * Приватный метод для выполнения общего кода удаления
     */
    private void deleteProductInDataBase(String productID) {
        if (productID != null) {
            try {
                new ProductSqlSteps().deleteProduct(productID);
            } catch (Exception e) {
                System.err.println("Ошибка при удалении продукта: " + e.getMessage());
            }
        }
    }
}