package tests.product;

import database.ProductSqlSteps;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import static helpers.AssertsHelper.assertProductDeletedBD;
import static helpers.DataHelper.*;
import static helpers.DataHelper.generateRandomQty;
import static io.restassured.RestAssured.given;

/**
 * Тестовый класс для проверки функциональности удаления продуктов.
 * Содержит тесты для проверки удаления продукта через API и корректности изменений в БД.
 */
@Epic("Product Management")
@Feature("Product Deletion")
public class DeleteProductByIdTest extends BaseTest {

    /**
     * Экземпляр для хранения запроса на создание продукта
     */
    private ProductCreateRequest productCreateRequest;

    /**
     * Экземпляр для хранения ответа на создание продукта
     */
    private ProductCreateResponse productCreateResponse;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестовый продукт для использования в тестах продукта.
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
     * Тест проверяет функциональность удаления продукта.
     * Выполняет запрос на удаление продукта и проверяет:
     * - Корректность статуса ответа
     * - Фактическое удаление продукта из БД
     */
    @Test
    @Description("Тестовый метод для удаления product, проверка в запросе api и в БД")
    public void productDeleteTest() {

        given()
                //.body(usersDeleteRequest)
                .when()
                .delete(config.deleteProductEndpoint() + productCreateResponse.getId())
                .then()
                .statusCode(STATUS_CODE_OK);

        assertProductDeletedBD(new ProductSqlSteps().getProductBDModel((productCreateResponse.getId())));
    }
}