package com.iict.buet.customer_portal.util;

import com.iict.buet.customer_portal.exceptions.InvalidMonthException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtils {
    private static final Logger logger = LogManager.getLogger(DateUtils.class.getName());

    private DateUtils() {

    }

    public static Integer getYearFromCurrentDate() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Date getExpirationTime(Long expireHours) {
        Date now = new Date();
        Long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMilis + now.getTime());
    }

    public static Integer getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Integer getMonthFromCurrentDate() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static String getStringMaturityDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStringDate(cal.getTime());
    }

    public static Boolean checkBetweenDaysLimit(String startDate, String endDate, long limit) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date firstDate = null;
        try {
            firstDate = sdf.parse(startDate);
            Date secondDate = sdf.parse(endDate);
            return checkBetweenDaysLimit(firstDate, secondDate, limit);
        } catch (ParseException e) {
            return false;
        }
    }

    public static List<Date> getListBetweenTwoDate(Date startDate, Date endDate) {
        List<Date> resultDateList = new ArrayList<>();
        Calendar startDateCalendar = new GregorianCalendar();
        startDateCalendar.setTime(startDate);
        Calendar endDateCalendar = new GregorianCalendar();
        endDateCalendar.setTime(endDate);
        while (startDateCalendar.before(endDateCalendar)) {
            Date resultDate = startDateCalendar.getTime();
            resultDateList.add(resultDate);
            startDateCalendar.add(Calendar.DATE, 1);
        }
        return resultDateList;
    }

    public static Map<String, Long> getPreviousMonthYear(Long month, Long year){
        return setMonthYear(month, year, -1);
    }

    public static Map<String, Long> getNextMonthYear(Long month, Long year){
        return setMonthYear(month, year, 1);
    }
    private static Map<String, Long> setMonthYear(Long month, Long year, int amount){
        Map<String, Long> resultMap = new HashMap<>();
        Date demoDate = getDateFromString("1/"+month+"/"+year, "dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        assert demoDate != null;
        calendar.setTime(demoDate);
        calendar.add(Calendar.MONTH, amount);
        resultMap.put("month", (long) (calendar.get(Calendar.MONTH) + 1));
        resultMap.put("year", (long) (calendar.get(Calendar.YEAR)));
        return resultMap;
    }

    public static Boolean isBeforeMonthYear(Long inMonth, Long inYear, Long month, Long year){
        YearMonth inMonthYear= YearMonth.of(Integer.parseInt(String.valueOf(inYear)), Integer.parseInt(String.valueOf(inMonth)));
        YearMonth monthYear= YearMonth.of(Integer.parseInt(String.valueOf(year)), Integer.parseInt(String.valueOf(month)));
        return inMonthYear.isBefore(monthYear);
    }

    public static Boolean isAfterMonthYear(Long inMonth, Long inYear, Long month, Long year) {
        YearMonth inMonthYear = YearMonth.of(Integer.parseInt(String.valueOf(inYear)), Integer.parseInt(String.valueOf(inMonth)));
        YearMonth monthYear = YearMonth.of(Integer.parseInt(String.valueOf(year)), Integer.parseInt(String.valueOf(month)));
        return inMonthYear.isAfter(monthYear);
    }

    public static Boolean checkBetweenDaysLimit(Date startDate, Date endDate, long limit) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long numOfDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return numOfDays <= limit;
    }

    public static Boolean checkBetweenDaysLimit(Date startDate, long limit) {
        return checkBetweenDaysLimit(startDate, new Date(), limit);
    }

    public static Boolean checkBetweenDaysLimit(String startDate, long limit) {
        Date firstDate = getDateFromString(startDate);
        return checkBetweenDaysLimit(firstDate, limit);
    }


    public static String getStringDate(Date date) {
        return getStringDate(date, "dd-MM-yyyy");
    }

    public static String getStringDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date getDateFromString(String date) {
        return getDateFromString(date, "dd-MM-yyyy");
    }

    public static Date getDateFromString(String date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            logger.error("exception in date format: " + e.getMessage());
            return null;
        }
    }
    public static Boolean isValidMonth(String month) {
        if (isValidNumber(month) && isMatchPattern(getNumberWithoutZeroPrefix(month), "^[1-9]{1}\\d{0,1}") && Integer.valueOf(getNumberWithoutZeroPrefix(month)) <= 12) {
            return true;
        }
        return false;

    }

    public static Boolean isValidYear(String year) {
        return isValidNumber(year) && isMatchPattern(getNumberWithoutZeroPrefix(year), "^[2-9]{1}\\d{3}") && getNumberWithoutZeroPrefix(year).length() == 4;
    }

    private static Boolean isValidNumber(String number) {
        try {
            return Integer.valueOf(number)>0;
        } catch (Exception e) {
            return false;
        }
    }

    private static Boolean isMatchPattern(String text, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }


    private static String getNumberWithoutZeroPrefix(String number) {
        Integer numberInt = Integer.valueOf(number);
        return String.valueOf(numberInt);
    }

    public static Boolean isValidMobileNo(String mobileNo) {
        return isValidNumber(mobileNo) && (isMatchPattern(mobileNo, "^[0][1]\\d{9}") || isMatchPattern(mobileNo, "^[0][3][1]\\d{6,7}"));
    }

    public static Boolean isValidDate(String date) {
        try {
            String[] dateArray = date.split("-");
            String day = dateArray[0];
            String month = dateArray[1];
            String year = dateArray[2];
            if (isValidMonth(month) && isValidYear(year) && (Integer.parseInt(day) <= maxDaysInMonth(Integer.valueOf(month), Integer.valueOf(year)))) {
                return getDateFromString(date, "dd-MM-yyyy") != null;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public static Boolean beforeOrEqual(Date firstDate, Date secondDate) {
        int dateMargin = DateUtils.getZeroTimeDate(firstDate).compareTo(DateUtils.getZeroTimeDate(secondDate));
        return dateMargin <= 0;
    }

    private static Boolean isLeapYear(Integer year) {
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }

    private static Integer maxDaysInMonth(Integer month, Integer year) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        if (month == 2 && isLeapYear(year)) {
            return 29;
        }
        if (month == 2 && !isLeapYear(year)) {
            return 28;
        }
        /*YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();*/
        return 30;
    }

    public static Boolean isAfterThreePM() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
        String hour = sdfHour.format(cal.getTime());

        String removedColonFromHour = hour.replaceAll("\\:", "");
        int hourOfThreePM = Integer.parseInt(removedColonFromHour);
        return hourOfThreePM >= 1500;
    }

    public static Integer getNumaricValueOfMonth(String monthName) throws InvalidMonthException{
        Date date = null;
        try {
            date = new SimpleDateFormat("MMMM").parse(monthName);
        } catch (ParseException e) {
            throw new InvalidMonthException("Invalid month");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        System.out.println(cal.get(Calendar.MONTH));
        return cal.get(Calendar.MONTH) + 1;
    }

    public static String getMonthNameFromNumericValue(Integer numericValue) throws InvalidMonthException {
        if (numericValue <= 12 && numericValue > 0) {
            return new DateFormatSymbols().getMonths()[numericValue - 1];
        }
        throw new InvalidMonthException("Invalid month");
    }

    public static String getTwoDigitYear(String fourDigitYear){
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yy");
        String demoStringDate = "1/1/"+fourDigitYear;
        Date demoDate = getDateFromString(demoStringDate, "dd/MM/yyyy");
        return simpleFormat.format(demoDate);
    }

    public static String getShortMonthNameFromNumericValue(String fullMonthName) throws InvalidMonthException {
        Integer numericValue = getNumaricValueOfMonth(fullMonthName);
        return getShortMonthNameFromNumericValue(numericValue);
    }

    public static String getShortMonthNameFromNumericValue(Integer numericValue) throws InvalidMonthException {
        if (numericValue <= 12 && numericValue > 0) {
            return new DateFormatSymbols().getShortMonths()[numericValue - 1];
        }
        throw new InvalidMonthException("Invalid month");
    }
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar today = Calendar.getInstance();

        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
}
