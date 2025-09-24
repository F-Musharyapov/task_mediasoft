package config;

import org.aeonbits.owner.Config;

/**
 * Интерфейс с основной конфигурацией проекта
 */
@Config.Sources({"classpath:config.properties"})
public interface BaseConfig extends Config {

    /**
     * Метод для возвращения значения параметра "адрес апи запрсоов baseUrl"
     *
     * @return API URL
     */
    String apiUrl();

    /**
     * Метод для возвращения значения параметра "адрес драйвера"
     *
     * @return адрес драйвера
     */
    String driverDb();

    /**
     * Метод для возвращения значения параметра адрес базы данных urlDb"
     *
     * @return адрес базы данных
     */
    String urlDb();

    /**
     * Метод для возвращения значения параметра "пользователь базы данных"
     *
     * @return пользователь
     */
    String userDb();

    /**
     * Метод для возвращения значения параметра "пароль базы данных"
     *
     * @return пароль
     */
    String passwordDb();

    /**
     * Метод для возвращения значения параметра "эндпоинт создания продукта"
     *
     * @return
     */
    String createProductEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт получения всех продуктов"
     *
     * @return
     */
    String allProductsEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт обновления продукта"
     *
     * @return
     */
    String updateProductEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт получения продукта"
     *
     * @return
     */
    String getProductByIdEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт удаления продукта"
     *
     * @return
     */
    String deleteProductEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт создания заказа"
     *
     * @return
     */
    String createOrderEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт получения заказа"
     *
     * @return
     */
    String getOrderByIdEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоинт удаления заказа"
     *
     * @return
     */
    String deleteOrderEndpoint();

    /**
     * Метод для возвращения значения параметра "эндпоин обновления заказа"
     *
     * @return
     */
    String updateOrderEndpoint();
}