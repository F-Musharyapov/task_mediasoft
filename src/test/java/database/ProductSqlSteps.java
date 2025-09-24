package database;

import config.BaseConfig;
import org.aeonbits.owner.ConfigFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс с методами для взаимодействия с БД сущности product
 */
public class ProductSqlSteps {

    /**
     * Экземпляр конфигурации
     */
    private static final BaseConfig config = ConfigFactory.create(BaseConfig.class, System.getenv());

    /**
     * Константы полей из БД
     */
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String ARTICLE_FIELD = "article";
    private static final String DICTIONARY_FIELD = "dictionary";
    private static final String CATEGORY_FIELD = "category";
    private static final String PRICE_FIELD = "price";
    private static final String QTY_FIELD = "qty";
    private static final String INSERTED_AT_FIELD = "inserted_at";
    private static final String LAST_QTY_CHANGED_FIELD = "last_qty_changed";
    private static final String IS_AVAILABLE_FIELD = "is_available";

    /**
     * Константы запросов в БД
     */
    private static final String SELECT_PRODUCT_SQL = "SELECT * FROM product WHERE %s = '%s'";
    private static final String SELECT_ID_PRODUCT_SQL = "SELECT id FROM product";
    private static final String DELETE_PRODUCT_SQL = "DELETE FROM product WHERE %s = '%s'";

    /**
     * Метод открытия подключения к базе данных
     *
     * @return экземпляр подключения
     */
    public static Connection getConnection() {
        try {
            Class.forName(config.driverDb());
            return DriverManager.getConnection(config.urlDb(), config.userDb(), config.passwordDb());
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    /**
     * Метод запроса в БД для получения данных Product
     *
     * @param id идентификатор поля, которое удаляем
     * @return экзепляр с необходимыми полями
     */
    public ProductBDModel getProductBDModel(String id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet result = stmt.executeQuery(String.format(SELECT_PRODUCT_SQL, ID_FIELD, id));
            if (result.next()) {
                return
                        ProductBDModel.builder()
                                .id(result.getString(ID_FIELD))
                                .name(result.getString(NAME_FIELD))
                                .article(result.getString(ARTICLE_FIELD))
                                .dictionary(result.getString(DICTIONARY_FIELD))
                                .category(result.getString(CATEGORY_FIELD))
                                .price(result.getBigDecimal(PRICE_FIELD))
                                .qty(result.getBigDecimal(QTY_FIELD))
                                .inserted_at(result.getString(INSERTED_AT_FIELD))
                                .last_qty_changed(result.getString(LAST_QTY_CHANGED_FIELD))
                                //.inserted_at(result.getObject(INSERTED_AT_FIELD, LocalDateTime.class))
                                //.last_qty_changed(result.getObject(LAST_QTY_CHANGED_FIELD, LocalDateTime.class))
                                .is_available(result.getString(IS_AVAILABLE_FIELD))
                                .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод запроса в БД для получения списка продуктов
     *
     * @return список продуктов
     */
    public List<String> setSelectIdProductSql() {
        List<String> selectIdProduct = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            ResultSet result = stmt.executeQuery(SELECT_ID_PRODUCT_SQL);

            while (result.next()) {
                String id = result.getString("id");
                selectIdProduct.add(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectIdProduct;
    }

    /**
     * Метод удаления product в БД
     *
     * @param id идентификатор поля, которое удаляем
     */
    public void deleteProduct(String id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(String.format(DELETE_PRODUCT_SQL, ID_FIELD, id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}