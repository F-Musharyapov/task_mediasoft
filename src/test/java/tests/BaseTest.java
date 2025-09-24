package tests;

import config.BaseConfig;
import helpers.BaseRequests;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

/**
 * Общий класс с настройками для всех тестов
 */
public class BaseTest {

    /**
     * Экземпляр конфигурации
     */
    protected static final BaseConfig config = ConfigFactory.create(BaseConfig.class, System.getenv());

    /**
     * Экземпляр класса BaseRequests с инициализированным RestAssured
     */
    private static BaseRequests baseRequests;

    /**
     * Экземпляр спецификации RestAssured
     */
    protected static RequestSpecification requestSpecification;

    /**
     * Метод инициализации объекта BaseRequests и спецификации запроса перед каждым тестом
     *
     * @throws IOException если ошибки при формировании спецификации
     */
    @BeforeAll
    public static void setup() throws IOException {
        if (baseRequests == null) {
            baseRequests = new BaseRequests(config);
        }
        requestSpecification = baseRequests.initRequestSpecification();
    }
}