import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Provides basic arithmetic operations using BigDecimal for precision.
 * Avoids floating-point errors (e.g., 0.1 + 0.2 == 0.3 exactly).
 */
public class Operations {

    /** 34-digit precision context — sufficient for all financial and scientific use cases. */
    static final MathContext MC = MathContext.DECIMAL128;

    /** Scale used for division results to avoid non-terminating decimal exceptions. */
    private static final int DIVISION_SCALE = 10;

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b, MC);
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b, MC);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b, MC);
    }

    /**
     * Divides a by b with DIVISION_SCALE decimal places.
     * @throws ArithmeticException if b is zero.
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero.");
        }
        return a.divide(b, DIVISION_SCALE, RoundingMode.HALF_UP);
    }
}
