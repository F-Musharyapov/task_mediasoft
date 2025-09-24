package tests.product;

import database.ProductSqlSteps;
import dto.product.ProductCreateRequest;
import dto.product.ProductCreateResponse;
import dto.product.ProductUpdateRequest;
import dto.product.ProductUpdateResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import static helpers.AssertsHelper.assertUpdateProductFieldsEqual;
import static helpers.DataHelper.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Тестовый класс для проверки функциональности обновления продуктов.
 * Содержит тесты для проверки обновления продукта через API и корректности изменений в БД.
 */
@Epic("Product Management")
@Feature("Product Update")
public class UpdateProductTest extends BaseTest {

    /**
     * Экземпляр для хранения запроса на создание продукта
     */
    private ProductCreateRequest productCreateRequest;

    /**
     * Экземпляр для хранения ответа на создание продукта
     */
    private ProductCreateResponse productCreateResponse;

    /**
     * Экземпляр для хранения запроса на обновление продукта
     */
    private ProductUpdateRequest productUpdateRequest;

    /**
     * Экземпляр для хранения ответа на обновление продукта
     */
    private ProductUpdateResponse productUpdateResponse;

    /**
     * Подготовительный метод, выполняемый перед каждым тестом.
     * Создает тестовый продукт для использования в тестах обновления продукта.
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
     * Тест проверяет функциональность обновления продукта.
     * Обновляет продукт и проверяет корректность обновления данных в базе данных.
     */
    @Test
    @Description("Тест обновления продукта: добавление нового продукта и проверка корректности данных")
    public void productUpdateTest() {
        productUpdateRequest = ProductUpdateRequest.builder()
                .name(getNameProduct())
                .id(productCreateResponse.getId())
                .article(String.valueOf(getUUID()))
                .category(getCategoryProduct())
                .dictionary(getDictionaryProduct())
                .price(generateRandomPrice())
                .qty(generateRandomQty())
                .build();

        productUpdateResponse = given()
                .spec(requestSpecification)
                .body(productUpdateRequest)
                .when()
                .patch(config.updateProductEndpoint())
                .then()
                .statusCode(STATUS_CODE_OK)
                .extract().as(ProductUpdateResponse.class);

        assertUpdateProductFieldsEqual(new ProductSqlSteps().getProductBDModel((productCreateResponse.getId())), productUpdateResponse);

        assertNotEquals(productCreateResponse.getLast_qty_changed(), productUpdateResponse.getLast_qty_changed(), "Поля не должны совпадать");
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