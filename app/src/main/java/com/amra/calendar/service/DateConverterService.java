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
        System.out.println(stringToConvert);
        LocalDate dateToConvert = LocalDate.parse(stringToConvert, FORMATTER);
        return convertToTUT(dateToConvert);
    }

    public String convertToTUT(LocalDate dateToConvert) {
        LocalDate baseDate = LocalDate.parse(TUT_FOUNDATION_DATE, FORMATTER);
        long numberOfDays = ChronoUnit.DAYS.between(baseDate, dateToConvert) + 1;

        System.out.println(numberOfDays);

        long baseValue = numberOfDays + CALENDAR_BASIS;
        String formattedDate = convertToTUTFormat(String.valueOf(baseValue));

        System.out.println(formattedDate);

        return formattedDate;
    }

    private String convertToTUTFormat(String days) {
        Pattern regexp = Pattern.compile("(\\d+)(\\d)(\\d)(\\d)");
        Matcher matcher = regexp.matcher(days);
        if (matcher.find()) {

            int heliadaDigit = parseInt(matcher.group(1));
            int gekatontadaDigit = parseInt(matcher.group(2));
            int decadaDigit = parseInt(matcher.group(3));
            int dayOfDecadaDigit = parseInt(matcher.group(4));

            // Going backwards from the last digit to the first
            String dayOfDecada = toRoman(dayOfDecadaDigit);
            if (dayOfDecadaDigit == 0) {
                decadaDigit -= 1;
            }
            String decada = toRoman(decadaDigit);
            if (decadaDigit <= 0) {
                gekatontadaDigit -= 1;
            }
            String gekatontada = toRoman(gekatontadaDigit);
            if (gekatontadaDigit <= 0) {
                heliadaDigit -= 1;
            }
            String heliada = toRoman(heliadaDigit);

            return format("%1$s.%2$s.%3$s.%4$s", heliada, gekatontada, decada, dayOfDecada);
        }
        return "";
    }

    public String convertToCommon(String tutFormatString) {
        Pattern regexp = Pattern.compile(TUT_FORMAT_REGEX);
        Matcher matcher = regexp.matcher(tutFormatString);
        String digits = "";
        if (matcher.find()) {

            String heliadaDigit = matcher.group(1);
            String gekatontadaDigit = matcher.group(2);
            String decadaDigit = matcher.group(3);
            String dayOfDecadaDigit = matcher.group(4);
            int heliada = toArabic(heliadaDigit);
            int decada = toArabic(decadaDigit);
            int gekatontada = toArabic(gekatontadaDigit);
            int dayOfDecada = toArabic(dayOfDecadaDigit);
            if (gekatontada >= 10) {
                gekatontada = 0;
                heliada += 1;
            }
            if (decada >= 10) {
                decada = 0;
                gekatontada += 1;
            }
            if (dayOfDecada == 10) {
                dayOfDecada = 0;
                decada += 1;
            }

            digits = format("%1$d%2$d%3$d%4$d", heliada, gekatontada, decada, dayOfDecada);
        }
        long parsedDigits = Long.parseLong(digits);
        System.out.println("Parsed " + parsedDigits);
        long days = parsedDigits - CALENDAR_BASIS;
        System.out.println("Days " + days);
        LocalDate baseDate = LocalDate.parse(TUT_FOUNDATION_DATE, FORMATTER);
        LocalDate dateInCommonFormat = baseDate.plusDays(days - 1);

        return FORMATTER.format(dateInCommonFormat);
    }
}
