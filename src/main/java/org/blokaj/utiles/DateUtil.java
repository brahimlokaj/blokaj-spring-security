package org.blokaj.utiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("DateUtil class");
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return new Date(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}
