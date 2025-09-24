package helpers;

import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс генерации данных для тестов
 */
public class DataHelper {

    /**
     * Экземпляр для Faker
     */
    private static final Faker faker = new Faker();

    /**
     * Экземпляр для Random
     */
    private static final Random random = new Random();

    /**
     * Константы кодов ответов выполнения запросов
     */
    public static final int STATUS_CODE_UPDATE = 204;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int STATUS_CODE_OK = 200;

    /**
     * Константы статусов заказов в БД
     */
    public static final String STATUS_ORDER_CREATED = "CREATED";
    public static final String STATUS_ORDER_CANCELLED = "CANCELLED";

    /**
     * Константы генерации данных для полей
     */
    public static final String[] NAME_PRODUCT = {"Sunny Honey", "Forest Berry", "Golden Nut", "Vanilla Cream", "Sea Salt", "Caramel Sphere", "Ripe Apple", "Chocolate Delight", "Fresh Lemonade", "Maple Essence"};
    public static final String[] CATEGORY_PRODUCT = {"FRUITS", "VEGETABLES"};
    public static final String DICTIONARY_PRODUCT = "[a-z]{8} [a-z]{6} [a-z]{4}";
    public static final String EMAIL_CUSTOMER = "[a-z]{10}\\@[a-z]{5}\\.[a-z]{2}";

    /**
     * Константа для параметра запроса customer_id
     */
    public static final String CUSTOMER_ID = "customer_id";

    /**
     * Метод генерации названия продукта
     *
     * @return сгенерированное название продукта
     */
    public static String getNameProduct() {
        return NAME_PRODUCT[random.nextInt(NAME_PRODUCT.length)];
    }

    /**
     * Метод генерации UUID
     *
     * @return сгенерированное UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Метод генерации категории продукта из представленных в CATEGORY_PRODUCT
     *
     * @return сгенерированная категория
     */
    public static String getCategoryProduct() {
        return CATEGORY_PRODUCT[random.nextInt(CATEGORY_PRODUCT.length)];
    }

    /**
     * Метод генерации Dictionary продукта
     *
     * @return сгенерированное Dictionary
     */
    public static String getDictionaryProduct() {
        return faker.regexify(DICTIONARY_PRODUCT);
    }

    /**
     * Метод генерации цены продукта
     *
     * @return сгенерированная цена
     */
    public static BigDecimal generateRandomPrice() {
        int cents = ThreadLocalRandom.current().nextInt(1, 100000);
        return BigDecimal.valueOf(cents, 2);
    }

    /**
     * Метод генерации количества продукта для метода создания продукта
     *
     * @return сгенерированное количество продукта
     */
    public static int generateRandomQty() {
        int randomValue = ThreadLocalRandom.current().nextInt(1, 51);
        return randomValue;
    }

    /**
     * Метод генерации количества продукта для оформления заказа
     *
     * @param maxQty максимальное количество продукта для заказа
     * @return сгенерированное количество для заказа
     */
    public static int generateRandomQtyForOrderCreateTest(int maxQty) {
        int maxAllowedQty = Math.min(maxQty, 10);
        return ThreadLocalRandom.current().nextInt(1, maxAllowedQty + 1);
    }

    /**
     * Метод генерации email для customer
     *
     * @return сгенерированный email
     */
    public static String getCustomerRandomEmail() {
        return faker.regexify(EMAIL_CUSTOMER);
    }

    /**
     * Метод генерации login для customer
     *
     * @return сгенерированный login
     */
    public static String getCustomerRandomNickName() {
        return faker.funnyName().name();
    }

    /**
     * Метод генерации адреса доставки
     *
     * @return сгенерированный адрес
     */
    public static String generateSimpleAddress() {
        return faker.address().streetAddressNumber() + " " +
                faker.address().streetName() + " " +
                faker.address().city() + " " +
                faker.address().zipCode().split("-")[0];
    }
}