package tests.product;

import database.ProductSqlSteps;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import dto.product.ProductGetByIdResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import static helpers.AssertsHelper.assertGetProductByIdFieldsEqual;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс для проверки функциональности получения информации о продукте.
 * Содержит тесты для проверки получения данных продукта через API и корректности данных в ответе.
 */
@Epic("Product Management")
@Feature("Get a product by ID")
public class GetProductByIdTest extends BaseTest {

    /**
     * Экземпляр для хранения запроса на создание продукта
     */
    private ProductCreateRequest productCreateRequest;

    /**
     * Экземпляр для хранения ответа на создание продукта
     */
    private ProductCreateResponse productCreateResponse;

    /**
     * Экземпляр для хранения ответа на получение данных продукта
     */
    private ProductGetByIdResponse productGetByIdResponse;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестовый продукт для использования в тестах.
     */
    @BeforeEach
    @Description("Подготовка тестовых данных: создание продукта")
    public void createProduct() {
        productCreateRequest = ProductCreateRequest.builder()
                .name(getNameProduct())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        productCreateResponse = given()
                .spec(requestSpecification)
                .body(productCreateRequest)
                .when()
                .post(config.createProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_CREATED)
                .extract().as(ProductCreateResponse.class);
    }

    /**
     * Тест проверяет функциональность получения информации о продукте.
     * Выполняет запрос на получение данных продукта и проверяет корректность ответа.
     */
    @Test
    @Description("Тест получения информации о продукте: проверка корректности данных в ответе")
    public void productGetByIdTest() {
        productGetByIdResponse = given()
                .spec(requestSpecification)
                .when()
                .get(config.getProductByIdEndpoint() + productCreateResponse.getId())
                .then()
                .statusCode(STATUS_CODE_OK)
                .extract().as(ProductGetByIdResponse.class);

        assertGetProductByIdFieldsEqual(new ProductSqlSteps().getProductBDModel((productCreateResponse.getId())), productGetByIdResponse);
    }

    /**
     * Метод очистки, выполняемый после каждого теста.
     * Удаляет созданный тестовый продукт из базы данных.
     * Обрабатывает возможные исключения при удалении.
     */
    @AfterEach
    @Description("Удаление Product после завершения тестов")
    public void deleteProductInDataBase() {
        if (productCreateResponse != null) {
            try {
                new ProductSqlSteps().deleteProduct((productCreateResponse.getId()));
            } catch (Exception e) {
                System.err.println("Ошибка при удалении продукта: " + e.getMessage());
            }
        }
    }
}