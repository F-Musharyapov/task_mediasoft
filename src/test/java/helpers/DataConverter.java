package helpers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс конвертации данных
 */
public class DataConverter {

    /**
     * Константа для единообразного форматирования дат-времени для API (без timezone)
     */
    private static final DateTimeFormatter API_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    /**
     * Метод конвертации даты из API в OffsetDateTime
     *
     * @param apiDateString строка даты из API, например "2025-09-14T16:01:17.92394"
     * @return OffsetDateTime в UTC
     */
    public static OffsetDateTime parseApiDateToUtcOffset(String apiDateString) {
        if (apiDateString == null) {
            throw new IllegalArgumentException("Поле с датой из API ответа не может быть null");
        }
        LocalDateTime localDateTime = LocalDateTime.parse(apiDateString, API_FORMATTER);
        return localDateTime.atOffset(ZoneOffset.UTC);
    }


    /**
     * Метод конвертации даты из DB в OffsetDateTime
     *
     * @param dbDateString строка даты из DB, например "2025-09-14 20:01:17.923 +0400"
     * @return OffsetDateTime в UTC
     */
    public static OffsetDateTime parseDbDateToUtcOffset(String dbDateString) {
        if (dbDateString == null) {
            throw new IllegalArgumentException("Поле с датой из бд не может быть null");
        }
        String isoFormatted = dbDateString.replaceFirst(" ", "T");
        return OffsetDateTime.parse(isoFormatted)
                .withOffsetSameInstant(ZoneOffset.UTC);
    }
}