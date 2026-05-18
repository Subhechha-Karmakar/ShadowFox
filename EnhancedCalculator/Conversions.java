import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Provides unit conversion utilities using BigDecimal for precision.
 * Covers temperature (Celsius, Fahrenheit, Kelvin) and currency (USD, EUR, GBP, INR).
 *
 * Currency rates are hardcoded constants using BigDecimal's String constructor
 * to avoid the double precision trap (e.g., new BigDecimal(0.92) != new BigDecimal("0.92")).
 */
public class Conversions {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final int TEMP_SCALE = 10;

    // ─── Temperature Constants ──────────────────────────────────────────
    private static final BigDecimal NINE = new BigDecimal("9");
    private static final BigDecimal FIVE = new BigDecimal("5");
    private static final BigDecimal THIRTY_TWO = new BigDecimal("32");
    private static final BigDecimal KELVIN_OFFSET = new BigDecimal("273.15");

    // ─── Temperature Conversions ────────────────────────────────────────

    public static BigDecimal celsiusToFahrenheit(BigDecimal celsius) {
        // (celsius * 9/5) + 32
        return celsius.multiply(NINE, MC)
                       .divide(FIVE, TEMP_SCALE, RoundingMode.HALF_UP)
                       .add(THIRTY_TWO, MC);
    }

    public static BigDecimal fahrenheitToCelsius(BigDecimal fahrenheit) {
        // (fahrenheit - 32) * 5/9
        return fahrenheit.subtract(THIRTY_TWO, MC)
                         .multiply(FIVE, MC)
                         .divide(NINE, TEMP_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal celsiusToKelvin(BigDecimal celsius) {
        return celsius.add(KELVIN_OFFSET, MC);
    }

    public static BigDecimal kelvinToCelsius(BigDecimal kelvin) {
        return kelvin.subtract(KELVIN_OFFSET, MC);
    }

    // ─── Currency Constants ─────────────────────────────────────────────
    // Rates against USD (1 USD = X foreign currency)
    // Using String constructor to avoid double precision loss
    private static final BigDecimal USD_TO_EUR = new BigDecimal("0.92");
    private static final BigDecimal USD_TO_GBP = new BigDecimal("0.79");
    private static final BigDecimal USD_TO_INR = new BigDecimal("83.50");

    // ─── Currency Conversion ────────────────────────────────────────────

    /**
     * Converts an amount from one currency to another via USD as the base.
     * Result is rounded to 2 decimal places (standard financial rounding).
     *
     * @param amount       the amount to convert
     * @param fromCurrency source currency code (USD, EUR, GBP, INR)
     * @param toCurrency   target currency code (USD, EUR, GBP, INR)
     * @return the converted amount with scale 2
     * @throws IllegalArgumentException if either currency code is unsupported
     */
    public static BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        // Step 1: Convert source amount to USD
        BigDecimal amountInUSD;
        switch (fromCurrency) {
            case "USD":
                amountInUSD = amount;
                break;
            case "EUR":
                amountInUSD = amount.divide(USD_TO_EUR, TEMP_SCALE, RoundingMode.HALF_UP);
                break;
            case "GBP":
                amountInUSD = amount.divide(USD_TO_GBP, TEMP_SCALE, RoundingMode.HALF_UP);
                break;
            case "INR":
                amountInUSD = amount.divide(USD_TO_INR, TEMP_SCALE, RoundingMode.HALF_UP);
                break;
            default:
                throw new IllegalArgumentException("Unsupported 'from' currency: " + fromCurrency);
        }

        // Step 2: Convert USD to target currency
        BigDecimal result;
        switch (toCurrency) {
            case "USD":
                result = amountInUSD;
                break;
            case "EUR":
                result = amountInUSD.multiply(USD_TO_EUR, MC);
                break;
            case "GBP":
                result = amountInUSD.multiply(USD_TO_GBP, MC);
                break;
            case "INR":
                result = amountInUSD.multiply(USD_TO_INR, MC);
                break;
            default:
                throw new IllegalArgumentException("Unsupported 'to' currency: " + toCurrency);
        }

        // Financial rounding: 2 decimal places
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
