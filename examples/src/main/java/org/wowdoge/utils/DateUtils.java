package org.wowdoge.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Provision of standard date and time handling</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public final class DateUtils {

    /**
     * Utility class should not have a public constructor
     */
    private DateUtils() {
    }

    public static DateTime nowUtc() {
        return new DateTime().withZone(DateTimeZone.UTC);
    }
}
