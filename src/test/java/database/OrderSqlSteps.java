package database;

import config.BaseConfig;
import org.aeonbits.owner.ConfigFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * Класс с методами для взаимодействия с БД сущности order
 */
public class OrderSqlSteps {

    /**
     * Экземпляр конфигурации
     */
    private static final BaseConfig config = ConfigFactory.create(BaseConfig.class, System.getenv());

    /**
     * Константы полей из БД
     */
    private static final String ORDER_ID_FIELD = "id";
    private static final String ORDERED_PRODUCT_ID_FIELD = "order_id ";
    private static final String CUSTOMER_ID_FIELD = "customer_id";
    private static final String ORDER_STATUS_FIELD = "status";
    private static final String ORDER_DEVILERY_ADDRESS_FIELD = "delivery_address";
    private static final String QTY_FIELD = "qty";
    private static final String PRODUCT_ID_FIELD = "product_id";
    private static final String PRODUCT_PRICE_FIELD = "price";
    private static final String PRODUCT_NAME_FIELD = "name";

    /**
     * Константы с запросами в БД
     */
    private static final String SELECT_ORDER_SQL = "SELECT * FROM \"order\" WHERE %s = '%s'";
    private static final String SELECT_ORDERED_PRODUCT_SQL2 = "SELECT o.order_id, o.product_id, o.qty, o.price, p.name FROM ordered_product o INNER JOIN product p ON p.id = o.product_id WHERE %s = '%s'";
    private static final String DELETE_ORDER_SQL = "DELETE FROM \"order\" WHERE %s = '%s'";
    private static final String DELETE_ORDERED_PRODUCT_SQL = "DELETE FROM ordered_product WHERE %s = '%s'";
    private static final String SELECT_ORDERED_PRODUCT_QTY_SQL = "SELECT qty FROM ordered_product WHERE order_id = '%s' AND product_id = '%s'";
    private static final String SELECT_ORDER_PRODUCT_STATUS_SQL = "SELECT status FROM \"order\" WHERE %s = '%s'";
    private static final String SELECT_ORDERED_PRODUCT_STATUS_SQL = "SELECT * FROM ordered_product WHERE %s = '%s'";
    private static final String CREATE_CUSTOMER_SQL = "INSERT INTO customer (login, email) VALUES ('%s', '%s');";
    private static final String SELECT_CUSTOMER_SQL = "SELECT id FROM customer WHERE login = '%s' AND email = '%s'";
    private static final String DELETE_CUSTOMER_SQL = "DELETE FROM customer WHERE %s = '%s'";

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
     * Метод запроса в БД для получения данных order
     *
     * @param id идентификатор поля, которое удаляем
     * @return экзепляр с необходимыми полями
     */
    public OrderBDModel getOrderBDModel(String id) {
        OrderBDModel order = null;
        try (Connection connection = getConnection()) {

            try (Statement orderStmt = connection.createStatement();
                 ResultSet result = orderStmt.executeQuery(String.format(SELECT_ORDER_SQL, ORDER_ID_FIELD, id))) {

                if (result.next()) {
                    order = OrderBDModel.builder()
                            .order_id(result.getString(ORDER_ID_FIELD))
                            .customer_id(result.getString(CUSTOMER_ID_FIELD))
                            .status(result.getString(ORDER_STATUS_FIELD))
                            .deliveryAddress(result.getString(ORDER_DEVILERY_ADDRESS_FIELD))
                            .products(new ArrayList<>())
                            .build();
                }
            }

            if (order != null) {
                try (Statement productsStmt = connection.createStatement();
                     ResultSet productsResult = productsStmt.executeQuery(String.format(SELECT_ORDERED_PRODUCT_SQL2, ORDERED_PRODUCT_ID_FIELD, id))) {

                    while (productsResult.next()) {
                        OrderBDModel.OrderProduct product = OrderBDModel.OrderProduct.builder()
                                .id(productsResult.getString(PRODUCT_ID_FIELD))
                                .qty(productsResult.getBigDecimal(QTY_FIELD).intValue())
                                .price(productsResult.getBigDecimal(PRODUCT_PRICE_FIELD))
                                .name(productsResult.getString(PRODUCT_NAME_FIELD))
                                .build();

                        order.getProducts().add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * Метод получения qty из таблицы ordered_product в БД
     *
     * @return qty
     */
    public Integer getQtyProductOrder(String order_id, String product_id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet result = stmt.executeQuery(String.format(SELECT_ORDERED_PRODUCT_QTY_SQL, order_id, product_id));
            if (result.next()) {
                return result.getInt("qty");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод удаления order в БД
     *
     * @param id идентификатор поля, которое удаляем
     */
    public void deleteOrder(String id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(String.format(DELETE_ORDERED_PRODUCT_SQL, ORDERED_PRODUCT_ID_FIELD, id));
            stmt.executeUpdate(String.format(DELETE_ORDER_SQL, ORDER_ID_FIELD, id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод получения статуса заказа из таблицы order
     *
     * @param id заказа
     * @return status
     */
    public String getStatusOrder(String id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet result = stmt.executeQuery(String.format(SELECT_ORDER_PRODUCT_STATUS_SQL, ORDER_ID_FIELD, id));
            if (result.next()) {
                return result.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод просмотра удаленности из таблицы order
     *
     * @param id заказа
     * @return если не удалился, то id, если удалился, то null
     */
    public String availabilityCheckOrder(String id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet result = stmt.executeQuery(String.format(SELECT_ORDERED_PRODUCT_STATUS_SQL, ORDERED_PRODUCT_ID_FIELD, id));
            if (result.next()) {
                return result.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод создания customer
     *
     * @param login
     * @param email
     * @return id customer если создался, если нет то null
     */
    public Integer createCustomer(String login, String email) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(String.format(CREATE_CUSTOMER_SQL, login, email));
            ResultSet result = stmt.executeQuery(String.format(SELECT_CUSTOMER_SQL, login, email));
            if (result.next()) {
                return result.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод удаления customer
     *
     * @param id customer
     */
    public void deleteCustomer(int id) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(String.format(DELETE_CUSTOMER_SQL, ORDER_ID_FIELD, id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}