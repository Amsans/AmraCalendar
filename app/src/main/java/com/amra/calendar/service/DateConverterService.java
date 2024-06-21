package com.amra.calendar.service;


import static com.amra.calendar.utils.RomanNumber.toArabic;
import static com.amra.calendar.utils.RomanNumber.toRoman;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateConverterService {

    public static final String TUT_FOUNDATION_DATE = "01/03/1996";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final DateTimeFormatter FORMATTER = ofPattern(DATE_PATTERN);
    public static final int CALENDAR_BASIS = 1110;
    // Roman like (XX).(I).(II).(IV)
    public static final String TUT_FORMAT_REGEX = "(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))" +
            "\\.((?:X{0,1})(?:IX|IV|V?I{0,3}))" +
            "\\.((?:X{0,1})(?:IX|IV|V?I{0,3}))" +
            "\\.((?:X{0,1})(?:IX|IV|V?I{0,3}))";

    public String convertToTUT(String stringToConvert) {
        LocalDate dateToConvert = LocalDate.parse(stringToConvert, FORMATTER);
        return convertToTUT(dateToConvert);
    }

    public String convertToTUT(LocalDate dateToConvert) {
        LocalDate baseDate = LocalDate.parse(TUT_FOUNDATION_DATE, FORMATTER);
        long numberOfDays = ChronoUnit.DAYS.between(baseDate, dateToConvert) + 1;
        long baseValue = numberOfDays + CALENDAR_BASIS;
        return convertToTUTFormat(String.valueOf(baseValue));
    }

    private String convertToTUTFormat(String days) {
        Pattern regexp = Pattern.compile("(\\d+)(\\d)(\\d)(\\d)");
        Matcher matcher = regexp.matcher(days);
        if (matcher.find()) {

            int chiliadDigit = parseInt(matcher.group(1));
            int hecatontadeDigit = parseInt(matcher.group(2));
            int decadeDigit = parseInt(matcher.group(3));
            int dayOfDecadeDigit = parseInt(matcher.group(4));

            // Going backwards from the last digit to the first
            String dayOfDecade = toRoman(dayOfDecadeDigit);
            if (dayOfDecadeDigit == 0) {
                decadeDigit -= 1;
            }
            String decade = toRoman(decadeDigit);
            if (decadeDigit <= 0) {
                hecatontadeDigit -= 1;
            }
            String hecatontade = toRoman(hecatontadeDigit);
            if (hecatontadeDigit <= 0) {
                chiliadDigit -= 1;
            }
            String chiliad = toRoman(chiliadDigit);

            return format("%1$s.%2$s.%3$s.%4$s", chiliad, hecatontade, decade, dayOfDecade);
        }
        return "";
    }

    public String convertToCommon(String tutFormatString) {
        Pattern regexp = Pattern.compile(TUT_FORMAT_REGEX);
        Matcher matcher = regexp.matcher(tutFormatString);
        String digits = "";
        if (matcher.find()) {

            String chiliadDigit = matcher.group(1);
            String hecatontadeDigit = matcher.group(2);
            String decadeDigit = matcher.group(3);
            String dayOfDecadeDigit = matcher.group(4);
            int chiliad = toArabic(chiliadDigit);
            int decade = toArabic(decadeDigit);
            int hecatontade = toArabic(hecatontadeDigit);
            int dayOfDecade = toArabic(dayOfDecadeDigit);
            if (hecatontade >= 10) {
                hecatontade = 0;
                chiliad += 1;
            }
            if (decade >= 10) {
                decade = 0;
                hecatontade += 1;
            }
            if (dayOfDecade == 10) {
                dayOfDecade = 0;
                decade += 1;
            }

            digits = format("%1$d%2$d%3$d%4$d", chiliad, hecatontade, decade, dayOfDecade);
        }
        long parsedDigits = Long.parseLong(digits);
        long days = parsedDigits - CALENDAR_BASIS;
        LocalDate baseDate = LocalDate.parse(TUT_FOUNDATION_DATE, FORMATTER);
        LocalDate dateInCommonFormat = baseDate.plusDays(days - 1);

        return FORMATTER.format(dateInCommonFormat);
    }
}
