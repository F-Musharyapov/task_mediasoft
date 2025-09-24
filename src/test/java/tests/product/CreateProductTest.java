package tests.product;

import database.ProductSqlSteps;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import static helpers.AssertsHelper.assertCreateProductFieldsEqual;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс для проверки функциональности создания продуктов.
 * Содержит тесты для проверки создания продукта через API и корректности изменений в БД.
 */
@Epic("Product Management")
@Feature("Product Creation")
public class CreateProductTest extends BaseTest {

    /**
     * Экземпляр для хранения запроса на создание продукта
     */
    private ProductCreateRequest productCreateRequest;

    /**
     * Экземпляр для хранения ответа на создание продукта
     */
    private ProductCreateResponse productCreateResponse;

    /**
     * Тест проверяет функциональность создания продукта.
     * Выполняет запрос на создание продукта и проверяет:
     * - Корректность статуса ответа
     * - Соответствие данных в ответе API данным в базе данных
     * - Корректность всех полей созданного продукта
     */
    @Test
    @Description("Тест создания продукта: проверка корректности данных в ответе API и БД")
    public void productCreateTest() {

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

        assertCreateProductFieldsEqual(new ProductSqlSteps().getProductBDModel((productCreateResponse.getId())), productCreateResponse);
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