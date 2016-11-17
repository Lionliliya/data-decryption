package com.gmail.liliya.yalovchenko;

/**
 * The class {@code DecoderUtil} contains methods for performing basic
 * validation and transforming operation for {@code DecoderUtil.class}
 * **/
public final class DecoderUtil {

    /**
     * Don't let anyone instantiate this class.
     */
    private DecoderUtil() {}

    /**Compute string representation of cargo name
     * @param   name StringBuilder representation of name in octal numbers
     *                format: one octal number - each 3 characters in string
     * @return name of cargo in StringBuilder representation
     */
    public static StringBuilder calculateCargoName(StringBuilder name) {
        StringBuilder result = new StringBuilder();
        StringBuilder octal = new StringBuilder();
        int index = 0;

        for (char c : name.toString().toCharArray()) {
            octal.append(c);
            index++;

            if (index == 3) {
                char letter = convertFromOctalToChar(octal);
                result.append(letter);
                index = 0;
                octal.delete(0, 4);
            }
        }
        return result;
    }

    /**
     * Define whether {@code ch} is number or not
     * @param ch character
     * @return {@code true} if ch represents number, otherwise {@code false}
     */
    public static boolean isDigit(char ch) {
        int digit = ch - '0';
        return 0 <= digit && digit <= 9;
    }

    /**
     * Define whether {@code ch} is + or -
     * @param ch character
     * @return {@code true} if ch represents + or -, otherwise {@code false}
     * **/
    public static boolean isSignOfTemp(char ch) {
        return ch == '+' || ch == '-';
    }

    /**
     * Define whether {@code ch} is R or r
     * @param ch character
     * @return {@code true} if ch represents R or r, otherwise {@code false}
     * **/
    public static boolean isR(char ch) {
        return ch == 'R' || ch == 'r';
    }

    /**
     * Convert octal number in string representation to letter of alphabet
     * @param octal octal number in StringBuilder format
     * @return {@code true} if ch represents R or r, otherwise {@code false}
     * @throws NumberFormatException, if {@code octal} consist letter
     * **/
    private static char convertFromOctalToChar(StringBuilder octal) {
        int decimal = Integer.parseInt(octal.toString(), 8);
        return (char) decimal;
    }

    /**
     * Remove leading zero in temperature
     * @param temperature temperature in StringBuilder format, possibly with leading zero
     * @return String temperature without leading zero, with + or -
     * **/
    public static String formatTemperature(StringBuilder temperature) {
        char sign = temperature.charAt(0);
        String temperatureWithOutZeros = temperature.deleteCharAt(0).toString().replaceFirst("^0+(?!$)", "");
        return sign + temperatureWithOutZeros;
    }
 }
