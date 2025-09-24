package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.BaseConfig;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

/**
 * Базовый тестовый класс с общими настройками для запросов REST API
 */
public class BaseRequests {

    /**
     * Экземпляр конфигурации с параметрами запроса
     */
    private final BaseConfig config;

    /**
     * Конструктор BaseConfig с инициализацией настройки RestAssured
     *
     * @param config с параметрами
     */
    public BaseRequests(BaseConfig config) {
        this.config = config;
        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) -> {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.registerModule(new JavaTimeModule());
                            return mapper;
                        }));
    }

    /**
     * Метод для получения спецификации RestAssured с базовыми настройками
     *
     * @return объект RequestSpecification с настройками
     * @throws IOException если ошибки при формировании спецификации
     */
    @Description("Создание базовой спецификации REST-запроса с настройками из конфигурации")
    public RequestSpecification initRequestSpecification() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(config.apiUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
        return requestSpecBuilder.build();
    }
}