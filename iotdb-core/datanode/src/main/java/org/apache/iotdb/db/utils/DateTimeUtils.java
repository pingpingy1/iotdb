/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.utils;

import org.apache.iotdb.commons.conf.CommonDescriptor;
import org.apache.iotdb.commons.utils.TestOnly;
import org.apache.iotdb.db.protocol.session.SessionManager;
import org.apache.iotdb.tsfile.utils.TimeDuration;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

  private DateTimeUtils() {
    // forbidding instantiation
  }

  public static final DateTimeFormatter ISO_LOCAL_DATE_WIDTH_1_2;

  static {
    ISO_LOCAL_DATE_WIDTH_1_2 =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 19, SignStyle.NEVER)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
            .toFormatter();
  }

  /** such as '2011/12/03'. */
  public static final DateTimeFormatter ISO_LOCAL_DATE_WITH_SLASH;

  static {
    ISO_LOCAL_DATE_WITH_SLASH =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 19, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
            .appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
            .toFormatter();
  }

  /** such as '2011.12.03'. */
  public static final DateTimeFormatter ISO_LOCAL_DATE_WITH_DOT;

  static {
    ISO_LOCAL_DATE_WITH_DOT =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 19, SignStyle.NEVER)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
            .appendLiteral('.')
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
            .toFormatter();
  }

  /** such as '10:15:30' or '10:15:30.123'. */
  public static final DateTimeFormatter ISO_LOCAL_TIME_WITH_MS;

  static {
    ISO_LOCAL_TIME_WITH_MS =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral('.')
            .appendValue(ChronoField.MILLI_OF_SECOND, 3)
            .optionalEnd()
            .toFormatter();
  }

  /** such as '10:15:30' or '10:15:30.123456'. */
  public static final DateTimeFormatter ISO_LOCAL_TIME_WITH_US;

  static {
    ISO_LOCAL_TIME_WITH_US =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral('.')
            .appendValue(ChronoField.MICRO_OF_SECOND, 6)
            .optionalEnd()
            .toFormatter();
  }

  /** such as '10:15:30' or '10:15:30.123456789'. */
  public static final DateTimeFormatter ISO_LOCAL_TIME_WITH_NS;

  static {
    ISO_LOCAL_TIME_WITH_NS =
        new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral('.')
            .appendValue(ChronoField.NANO_OF_SECOND, 9)
            .optionalEnd()
            .toFormatter();
  }

  /** such as '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_MS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_MS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WIDTH_1_2)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WIDTH_1_2)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WIDTH_1_2)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SPACE;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SPACE =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SPACE_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SPACE_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SPACE_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SPACE_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_SLASH)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_MS)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123456+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_US;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_US =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_US)
            .appendOffsetId()
            .toFormatter();
  }

  /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123456789+01:00'. */
  public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_NS;

  static {
    ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_NS =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_WITH_DOT)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME_WITH_NS)
            .appendOffsetId()
            .toFormatter();
  }

  public static final DateTimeFormatter formatter =
      new DateTimeFormatterBuilder()
          /**
           * The ISO date-time formatter that formats or parses a date-time with an offset, such as
           * '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123+01:00'.
           */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_MS)

          /** such as '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_US)

          /** such as '2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_NS)

          /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH)

          /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH_US)

          /** such as '2011/12/03T10:15:30+01:00' or '2011/12/03T10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH_NS)

          /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT)

          /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT_US)

          /** such as '2011.12.03T10:15:30+01:00' or '2011.12.03T10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT_NS)

          /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SPACE)

          /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SPACE_US)

          /** such as '2011-12-03 10:15:30+01:00' or '2011-12-03 10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SPACE_NS)

          /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE)

          /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_US)

          /** such as '2011/12/03 10:15:30+01:00' or '2011/12/03 10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_SLASH_WITH_SPACE_NS)

          /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE)

          /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123456+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_US)

          /** such as '2011.12.03 10:15:30+01:00' or '2011.12.03 10:15:30.123456789+01:00'. */
          .appendOptional(ISO_OFFSET_DATE_TIME_WITH_DOT_WITH_SPACE_NS)
          .toFormatter();

  public static long convertTimestampOrDatetimeStrToLongWithDefaultZone(String timeStr) {
    try {
      return Long.parseLong(timeStr);
    } catch (NumberFormatException e) {
      return DateTimeUtils.convertDatetimeStrToLong(timeStr, ZoneId.systemDefault());
    }
  }

  public static long convertDatetimeStrToLong(String str, ZoneId zoneId) {
    return convertDatetimeStrToLong(
        str,
        toZoneOffset(zoneId),
        0,
        CommonDescriptor.getInstance().getConfig().getTimestampPrecision());
  }

  public static long convertDatetimeStrToLong(
      String str, ZoneId zoneId, String timestampPrecision) {
    return convertDatetimeStrToLong(str, toZoneOffset(zoneId), 0, timestampPrecision);
  }

  public static long getInstantWithPrecision(String str, String timestampPrecision) {
    try {
      ZonedDateTime zonedDateTime = ZonedDateTime.parse(str, formatter);
      Instant instant = zonedDateTime.toInstant();
      if ("us".equals(timestampPrecision)) {
        if (instant.getEpochSecond() < 0 && instant.getNano() > 0) {
          // adjustment can reduce the loss of the division
          long millis = Math.multiplyExact(instant.getEpochSecond() + 1, 1000_000L);
          long adjustment = instant.getNano() / 1000 - 1L;
          return Math.addExact(millis, adjustment);
        } else {
          long millis = Math.multiplyExact(instant.getEpochSecond(), 1000_000L);
          return Math.addExact(millis, instant.getNano() / 1000);
        }
      } else if ("ns".equals(timestampPrecision)) {
        long millis = Math.multiplyExact(instant.getEpochSecond(), 1000_000_000L);
        return Math.addExact(millis, instant.getNano());
      }
      return instant.toEpochMilli();
    } catch (DateTimeParseException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /** convert date time string to millisecond, microsecond or nanosecond. */
  public static long convertDatetimeStrToLong(
      String str, ZoneOffset offset, int depth, String timestampPrecision) {
    if (depth >= 2) {
      throw new DateTimeException(
          String.format(
              "Failed to convert %s to millisecond, zone offset is %s, "
                  + "please input like 2011-12-03T10:15:30 or 2011-12-03T10:15:30+01:00",
              str, offset));
    }
    if (str.contains("Z")) {
      return convertDatetimeStrToLong(
          str.substring(0, str.indexOf('Z')) + "+00:00", offset, depth, timestampPrecision);
    } else if (str.length() == 10) {
      return convertDatetimeStrToLong(str + "T00:00:00", offset, depth, timestampPrecision);
    } else if (str.length() - str.lastIndexOf('+') != 6
        && str.length() - str.lastIndexOf('-') != 6) {
      return convertDatetimeStrToLong(str + offset, offset, depth + 1, timestampPrecision);
    } else if (str.contains("[") || str.contains("]")) {
      throw new DateTimeException(
          String.format(
              "%s with [time-region] at end is not supported now, "
                  + "please input like 2011-12-03T10:15:30 or 2011-12-03T10:15:30+01:00",
              str));
    }
    return getInstantWithPrecision(str, timestampPrecision);
  }

  /**
   * Convert duration string to time value. CurrentTime is used to calculate the days of natural
   * month. If it's set as -1, which means a context free situation, then '1mo' will be thought as
   * 30 days.
   *
   * @param duration represent duration string like: 12d8m9ns, 1y1mo, etc.
   * @return time in milliseconds, microseconds, or nanoseconds depending on the profile
   */
  public static long convertDurationStrToLong(String duration) {
    return convertDurationStrToLong(-1, duration, false);
  }

  public static long convertDurationStrToLong(String duration, boolean convertYearToMonth) {
    return convertDurationStrToLong(-1, duration, convertYearToMonth);
  }

  public static long convertDurationStrToLong(
      String duration, String timestampPrecision, boolean convertYearToMonth) {
    return convertDurationStrToLong(-1, duration, timestampPrecision, convertYearToMonth);
  }

  public static long convertDurationStrToLong(
      long currentTime, String duration, boolean convertYearToMonth) {
    return convertDurationStrToLong(
        currentTime,
        duration,
        CommonDescriptor.getInstance().getConfig().getTimestampPrecision(),
        convertYearToMonth);
  }

  /**
   * convert duration string to time value.
   *
   * @param duration represent duration string like: 12d8m9ns, 1y1mo, etc.
   * @param convertYearToMonth if we need convert year to month. eg: 1y -> 12mo
   * @return time in milliseconds, microseconds, or nanoseconds depending on the profile
   */
  public static long convertDurationStrToLong(
      long currentTime, String duration, String timestampPrecision, boolean convertYearToMonth) {
    long total = 0;
    long temp = 0;
    for (int i = 0; i < duration.length(); i++) {
      char ch = duration.charAt(i);
      if (Character.isDigit(ch)) {
        temp *= 10;
        temp += (ch - '0');
      } else {
        String unit = String.valueOf(duration.charAt(i));
        // This is to identify units with two letters.
        if (i + 1 < duration.length() && !Character.isDigit(duration.charAt(i + 1))) {
          i++;
          unit += duration.charAt(i);
        }
        unit = unit.toLowerCase();
        if (convertYearToMonth && unit.equals("y")) {
          temp *= 12;
          unit = "mo";
        }
        total +=
            DateTimeUtils.convertDurationStrToLong(
                currentTime == -1 ? -1 : currentTime + total, temp, unit, timestampPrecision);
        temp = 0;
      }
    }
    return total;
  }

  @TestOnly
  public static long convertDurationStrToLongForTest(
      long value, String unit, String timestampPrecision) {
    return convertDurationStrToLong(-1, value, unit, timestampPrecision);
  }

  /** convert duration string to millisecond, microsecond or nanosecond. */
  public static long convertDurationStrToLong(
      long currentTime, long value, String unit, String timestampPrecision) {
    DurationUnit durationUnit = DurationUnit.valueOf(unit);
    long res = value;
    switch (durationUnit) {
      case y:
        res *= 365 * 86_400_000L;
        break;
      case mo:
        if (currentTime == -1) {
          res *= 30 * 86_400_000L;
        } else {
          Calendar calendar = Calendar.getInstance();
          calendar.setTimeZone(SessionManager.getInstance().getSessionTimeZone());
          calendar.setTimeInMillis(currentTime);
          calendar.add(Calendar.MONTH, (int) (value));
          res = calendar.getTimeInMillis() - currentTime;
        }
        break;
      case w:
        res *= 7 * 86_400_000L;
        break;
      case d:
        res *= 86_400_000L;
        break;
      case h:
        res *= 3_600_000L;
        break;
      case m:
        res *= 60_000L;
        break;
      case s:
        res *= 1_000L;
        break;
      default:
        break;
    }

    if ("us".equals(timestampPrecision)) {
      if (unit.equals(DurationUnit.ns.toString())) {
        return value / 1000;
      } else if (unit.equals(DurationUnit.us.toString())) {
        return value;
      } else {
        return res * 1000;
      }
    } else if ("ns".equals(timestampPrecision)) {
      if (unit.equals(DurationUnit.ns.toString())) {
        return value;
      } else if (unit.equals(DurationUnit.us.toString())) {
        return value * 1000;
      } else {
        return res * 1000_000;
      }
    } else {
      if (unit.equals(DurationUnit.ns.toString())) {
        return value / 1000_000;
      } else if (unit.equals(DurationUnit.us.toString())) {
        return value / 1000;
      } else {
        return res;
      }
    }
  }

  public static TimeUnit timestampPrecisionStringToTimeUnit(String timestampPrecision) {
    if ("us".equals(timestampPrecision)) {
      return TimeUnit.MICROSECONDS;
    } else if ("ns".equals(timestampPrecision)) {
      return TimeUnit.NANOSECONDS;
    } else {
      return TimeUnit.MILLISECONDS;
    }
  }

  public static String convertLongToDate(long timestamp) {
    return convertLongToDate(
        timestamp, CommonDescriptor.getInstance().getConfig().getTimestampPrecision());
  }

  public static String convertLongToDate(long timestamp, String sourcePrecision) {
    switch (sourcePrecision) {
      case "ns":
        timestamp /= 1000_000;
        break;
      case "us":
        timestamp /= 1000;
        break;
    }
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        .toString();
  }

  public static String convertMillisecondToDurationStr(long millisecond) {
    Duration duration = Duration.ofMillis(millisecond);
    long days = duration.toDays();
    long years = days / 365;
    days = days % 365;
    long months = days / 30;
    days %= 30;
    long hours = duration.toHours() % 24;
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;
    StringBuilder result = new StringBuilder();
    if (years > 0) {
      result.append(years).append(" years ");
    }
    if (months > 0) {
      result.append(months).append(" months ");
    }
    if (days > 0) {
      result.append(days).append(" days ");
    }
    result.append(hours).append(" hours ");
    result.append(minutes).append(" minutes ");
    result.append(seconds).append(" seconds");
    return result.toString();
  }

  public static ZoneOffset toZoneOffset(ZoneId zoneId) {
    return zoneId.getRules().getOffset(Instant.now());
  }

  public static ZonedDateTime convertMillsecondToZonedDateTime(long millisecond) {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneId.systemDefault());
  }

  public enum DurationUnit {
    y,
    mo,
    w,
    d,
    h,
    m,
    s,
    ms,
    us,
    ns
  }

  public static TimeUnit toTimeUnit(String t) {
    switch (t) {
      case "h":
        return TimeUnit.HOURS;
      case "m":
        return TimeUnit.MINUTES;
      case "s":
        return TimeUnit.SECONDS;
      case "ms":
        return TimeUnit.MILLISECONDS;
      case "u":
        return TimeUnit.MICROSECONDS;
      case "n":
        return TimeUnit.NANOSECONDS;
      default:
        throw new IllegalArgumentException("time precision must be one of: h,m,s,ms,u,n");
    }
  }

  public static final long MS_TO_MONTH = 30 * 86400_000L;

  public static long calcPositiveIntervalByMonth(long startTime, TimeDuration duration) {
    return TimeDuration.calcPositiveIntervalByMonth(
        startTime,
        duration,
        SessionManager.getInstance().getSessionTimeZone(),
        TimestampPrecisionUtils.currPrecision);
  }

  /**
   * Storage the duration into two parts: month part and non-month part, the non-month part's
   * precision is depended on current time precision. e.g. ms precision: '1y1mo1ms' -> monthDuration
   * = 13, nonMonthDuration = 1, ns precision: '1y1mo1ms' -> monthDuration = 13, nonMonthDuration =
   * 1000_000.
   *
   * @param duration the input duration string
   * @return the TimeDuration instance contains month part and non-month part
   */
  public static TimeDuration constructTimeDuration(String duration) {
    duration = duration.toLowerCase();
    String currTimePrecision = CommonDescriptor.getInstance().getConfig().getTimestampPrecision();
    int temp = 0;
    int monthDuration = 0;
    long nonMonthDuration = 0;
    for (int i = 0; i < duration.length(); i++) {
      char ch = duration.charAt(i);
      if (Character.isDigit(ch)) {
        temp *= 10;
        temp += (ch - '0');
      } else {
        String unit = String.valueOf(duration.charAt(i));
        // This is to identify units with two letters.
        if (i + 1 < duration.length() && !Character.isDigit(duration.charAt(i + 1))) {
          i++;
          unit += duration.charAt(i);
        }
        if (unit.equals("y")) {
          monthDuration += temp * 12;
          temp = 0;
          continue;
        }
        if (unit.equals("mo")) {
          monthDuration += temp;
          temp = 0;
          continue;
        }
        nonMonthDuration +=
            DateTimeUtils.convertDurationStrToLong(-1, temp, unit, currTimePrecision);
        temp = 0;
      }
    }
    return new TimeDuration(monthDuration, nonMonthDuration);
  }
}
