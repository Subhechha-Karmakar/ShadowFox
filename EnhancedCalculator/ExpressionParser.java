import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A recursive descent parser that evaluates mathematical expressions
 * with correct BODMAS/PEMDAS operator precedence using BigDecimal.
 *
 * Grammar:
 *   expression → term (('+' | '-') term)*
 *   term       → factor (('*' | '/') factor)*
 *   factor     → unary ('^' factor)?          // right-associative
 *   unary      → '-' unary | primary
 *   primary    → NUMBER | '(' expression ')' | 'sqrt(' expression ')'
 *
 * Examples:
 *   "5 + 3 * 2"         → 11  (not 16)
 *   "2 ^ 3 ^ 2"         → 512 (right-associative: 2^(3^2) = 2^9)
 *   "sqrt(16) + 1"      → 5
 *   "(2 + 3) * (4 - 1)" → 15
 *
 * Shared by both the console Calculator and the Swing CalculatorGUI.
 */
public class ExpressionParser {

    private final String expr;
    private int pos;

    public ExpressionParser(String expr) {
        this.expr = expr;
        this.pos = 0;
    }

    /**
     * Parses the entire expression and ensures all input is consumed.
     */
    public BigDecimal parse() {
        BigDecimal result = parseExpression();
        skipWhitespace();
        if (pos < expr.length()) {
            throw new RuntimeException(
                    "Unexpected character '" + expr.charAt(pos) + "' at position " + (pos + 1));
        }
        return result;
    }

    // ── expression → term (('+' | '-') term)* ──────────────────

    private BigDecimal parseExpression() {
        BigDecimal left = parseTerm();

        while (pos < expr.length()) {
            skipWhitespace();
            if (pos >= expr.length()) break;

            char op = expr.charAt(pos);
            if (op != '+' && op != '-') break;

            pos++; // consume operator
            BigDecimal right = parseTerm();

            if (op == '+') {
                left = Operations.add(left, right);
            } else {
                left = Operations.subtract(left, right);
            }
        }
        return left;
    }

    // ── term → factor (('*' | '/') factor)* ────────────────────

    private BigDecimal parseTerm() {
        BigDecimal left = parseFactor();

        while (pos < expr.length()) {
            skipWhitespace();
            if (pos >= expr.length()) break;

            char op = expr.charAt(pos);
            // Support both × and * for multiplication, ÷ and / for division
            if (op != '*' && op != '/' && op != '×' && op != '÷') break;

            pos++; // consume operator
            BigDecimal right = parseFactor();

            if (op == '*' || op == '×') {
                left = Operations.multiply(left, right);
            } else {
                left = Operations.divide(left, right);
            }
        }
        return left;
    }

    // ── factor → unary ('^' factor)? ───────────────────────────
    // Right-associative: 2^3^2 = 2^(3^2) = 512

    private BigDecimal parseFactor() {
        BigDecimal left = parseUnary();

        skipWhitespace();
        if (pos < expr.length() && expr.charAt(pos) == '^') {
            pos++; // consume '^'
            BigDecimal right = parseFactor(); // recursive call for right-associativity
            return Scientific.exponentiation(left, right);
        }
        return left;
    }

    // ── unary → '-' unary | primary ────────────────────────────

    private BigDecimal parseUnary() {
        skipWhitespace();
        if (pos < expr.length() && expr.charAt(pos) == '-') {
            pos++; // consume '-'
            BigDecimal operand = parseUnary();
            return operand.negate();
        }
        return parsePrimary();
    }

    // ── primary → NUMBER | '(' expression ')' | 'sqrt(' expression ')' ─

    private BigDecimal parsePrimary() {
        skipWhitespace();

        if (pos >= expr.length()) {
            throw new RuntimeException("Unexpected end of expression.");
        }

        // Handle sqrt(...)
        if (pos + 4 < expr.length()
                && expr.substring(pos, pos + 4).equalsIgnoreCase("sqrt")) {
            pos += 4; // consume "sqrt"
            skipWhitespace();
            if (pos >= expr.length() || expr.charAt(pos) != '(') {
                throw new RuntimeException("Expected '(' after 'sqrt' at position " + (pos + 1));
            }
            pos++; // consume '('
            BigDecimal inner = parseExpression();
            skipWhitespace();
            if (pos >= expr.length() || expr.charAt(pos) != ')') {
                throw new RuntimeException("Missing closing ')' for sqrt at position " + (pos + 1));
            }
            pos++; // consume ')'
            return Scientific.squareRoot(inner);
        }

        // Handle parenthesized expression: (expression)
        if (expr.charAt(pos) == '(') {
            pos++; // consume '('
            BigDecimal inner = parseExpression();
            skipWhitespace();
            if (pos >= expr.length() || expr.charAt(pos) != ')') {
                throw new RuntimeException("Missing closing ')' at position " + (pos + 1));
            }
            pos++; // consume ')'
            return inner;
        }

        // Handle number
        return parseNumber();
    }

    // ── Number parsing ──────────────────────────────────────────

    private BigDecimal parseNumber() {
        skipWhitespace();
        int start = pos;

        // Consume digits and at most one decimal point
        boolean hasDecimalPoint = false;
        while (pos < expr.length()) {
            char c = expr.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.' && !hasDecimalPoint) {
                hasDecimalPoint = true;
                pos++;
            } else {
                break;
            }
        }

        if (pos == start) {
            throw new RuntimeException(
                    "Expected a number at position " + (pos + 1)
                    + " but found '" + (pos < expr.length() ? expr.charAt(pos) : "end") + "'");
        }

        String numberStr = expr.substring(start, pos);
        try {
            return new BigDecimal(numberStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number: '" + numberStr + "'");
        }
    }

    // ── Whitespace handling ─────────────────────────────────────

    private void skipWhitespace() {
        while (pos < expr.length() && Character.isWhitespace(expr.charAt(pos))) {
            pos++;
        }
    }
}
