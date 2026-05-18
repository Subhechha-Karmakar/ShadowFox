import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Provides scientific calculation operations using BigDecimal.
 * Java 8 compatible — uses Math.sqrt() wrapped in BigDecimal since
 * BigDecimal.sqrt(MathContext) is only available in Java 9+.
 */
public class Scientific {

    private static final MathContext MC = MathContext.DECIMAL128;

    /**
     * Calculates the square root of a BigDecimal value.
     * Uses Math.sqrt() internally since BigDecimal.sqrt() requires Java 9+.
     *
     * @param a the value to take the square root of
     * @return the square root as a BigDecimal
     * @throws ArithmeticException if a is negative
     */
    public static BigDecimal squareRoot(BigDecimal a) {
        if (a.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Cannot calculate the square root of a negative number.");
        }
        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Java 8 fallback: use Math.sqrt() and convert via String to preserve precision
        double sqrtDouble = Math.sqrt(a.doubleValue());
        return new BigDecimal(String.valueOf(sqrtDouble), MC);
    }

    /**
     * Raises base to an integer exponent using BigDecimal.pow().
     * Handles negative exponents as 1 / base^|exponent|.
     *
     * @param base     the base value
     * @param exponent the integer exponent
     * @return base raised to the power of exponent
     */
    public static BigDecimal exponentiation(BigDecimal base, int exponent) {
        if (exponent >= 0) {
            return base.pow(exponent, MC);
        } else {
            // base^(-n) = 1 / base^n
            BigDecimal powered = base.pow(-exponent, MC);
            if (powered.compareTo(BigDecimal.ZERO) == 0) {
                throw new ArithmeticException("Cannot raise zero to a negative power.");
            }
            return BigDecimal.ONE.divide(powered, MC.getPrecision(), RoundingMode.HALF_UP);
        }
    }

    /**
     * Raises base to a BigDecimal exponent.
     * If the exponent is an exact integer, delegates to the precise BigDecimal.pow().
     * Otherwise, falls back to Math.pow() (documented precision trade-off for fractional exponents).
     *
     * @param base     the base value
     * @param exponent the exponent (can be fractional)
     * @return base raised to the power of exponent
     */
    public static BigDecimal exponentiation(BigDecimal base, BigDecimal exponent) {
        try {
            // Try exact integer conversion first for maximum precision
            int intExponent = exponent.intValueExact();
            return exponentiation(base, intExponent);
        } catch (ArithmeticException e) {
            // Fractional exponent: fall back to Math.pow() (documented precision trade-off)
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            if (Double.isNaN(result)) {
                throw new ArithmeticException("Result is undefined (NaN).");
            }
            if (Double.isInfinite(result)) {
                throw new ArithmeticException("Result is too large (Infinity).");
            }
            return new BigDecimal(String.valueOf(result), MC);
        }
    }
}
