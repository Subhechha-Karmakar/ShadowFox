import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Scanner;

/**
 * Console-Based Calculator with BigDecimal precision.
 *
 * Features:
 *   1. Basic Arithmetic (add, subtract, multiply, divide)
 *   2. Scientific Calculations (square root, exponentiation)
 *   3. Unit Conversions (temperature, currency)
 *   4. Expression Parser with BODMAS/PEMDAS precedence
 *
 * Engineering Concepts Demonstrated:
 *   - BigDecimal over double/float to avoid floating-point errors (0.1 + 0.2 == 0.3)
 *   - Exception handling (InputMismatchException, ArithmeticException, NumberFormatException)
 *   - Continuous loop with "Do you want to continue?" behavior
 *   - Recursive descent parsing for mathematical expression evaluation
 */
public class Calculator {

    private static final MathContext MC = MathContext.DECIMAL128;

    // ═══════════════════════════════════════════════════════════════════
    //  MAIN ENTRY POINT
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println();
        System.out.println("==================================================");
        System.out.println("       CONSOLE-BASED CALCULATOR (BigDecimal)      ");
        System.out.println("       Precision You Can Trust: 0.1 + 0.2 = 0.3  ");
        System.out.println("==================================================");

        while (running) {
            System.out.println();
            System.out.println("--- Main Menu ---");
            System.out.println("1. Basic Arithmetic");
            System.out.println("2. Scientific Calculations");
            System.out.println("3. Unit Conversions");
            System.out.println("4. Expression Parser (BODMAS/PEMDAS)");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a number between 1 and 5.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleArithmetic(scanner);
                    break;
                case 2:
                    handleScientific(scanner);
                    break;
                case 3:
                    handleConversions(scanner);
                    break;
                case 4:
                    handleExpression(scanner);
                    break;
                case 5:
                    running = false;
                    System.out.println();
                    System.out.println("Exiting Calculator. Goodbye!");
                    break;
                default:
                    System.out.println("[ERROR] Invalid choice. Please select from 1 to 5.");
            }
        }
        scanner.close();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  UTILITY: Format BigDecimal for clean display
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Formats a BigDecimal result for display.
     * Strips trailing zeros and uses toPlainString() to avoid scientific notation.
     * Example: 0.30 -> "0.3", 100 -> "100" (not "1E+2")
     */
    private static String formatResult(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    /**
     * Safely reads a BigDecimal from user input.
     * @param scanner the Scanner to read from
     * @param prompt  the prompt to display
     * @return the parsed BigDecimal, or null if input was invalid
     */
    private static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return new BigDecimal(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number format. Please enter a valid number.");
            return null;
        }
    }

    /**
     * Safely reads an integer menu choice from user input.
     * @return the parsed integer, or -1 if invalid
     */
    private static int readMenuChoice(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input. Please enter a number.");
            return -1;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HANDLER: Basic Arithmetic
    // ═══════════════════════════════════════════════════════════════════

    private static void handleArithmetic(Scanner scanner) {
        System.out.println();
        System.out.println("--- Basic Arithmetic ---");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraction (-)");
        System.out.println("3. Multiplication (*)");
        System.out.println("4. Division (/)");

        int choice = readMenuChoice(scanner, "Choose an operation (1-4): ");
        if (choice < 1 || choice > 4) {
            System.out.println("[ERROR] Invalid choice.");
            return;
        }

        BigDecimal a = readBigDecimal(scanner, "Enter first number: ");
        if (a == null) return;
        BigDecimal b = readBigDecimal(scanner, "Enter second number: ");
        if (b == null) return;

        try {
            BigDecimal result;
            String operator;
            switch (choice) {
                case 1:
                    result = Operations.add(a, b);
                    operator = "+";
                    break;
                case 2:
                    result = Operations.subtract(a, b);
                    operator = "-";
                    break;
                case 3:
                    result = Operations.multiply(a, b);
                    operator = "*";
                    break;
                case 4:
                    result = Operations.divide(a, b);
                    operator = "/";
                    break;
                default:
                    return;
            }
            System.out.println("Result: " + formatResult(a) + " " + operator + " "
                    + formatResult(b) + " = " + formatResult(result));
        } catch (ArithmeticException e) {
            System.out.println("[MATH ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HANDLER: Scientific Calculations
    // ═══════════════════════════════════════════════════════════════════

    private static void handleScientific(Scanner scanner) {
        System.out.println();
        System.out.println("--- Scientific Calculations ---");
        System.out.println("1. Square Root");
        System.out.println("2. Exponentiation (Power)");

        int choice = readMenuChoice(scanner, "Choose an operation (1-2): ");

        try {
            if (choice == 1) {
                BigDecimal a = readBigDecimal(scanner, "Enter number: ");
                if (a == null) return;
                BigDecimal result = Scientific.squareRoot(a);
                System.out.println("Result: sqrt(" + formatResult(a) + ") = " + formatResult(result));

            } else if (choice == 2) {
                BigDecimal base = readBigDecimal(scanner, "Enter base: ");
                if (base == null) return;
                BigDecimal exp = readBigDecimal(scanner, "Enter exponent: ");
                if (exp == null) return;
                BigDecimal result = Scientific.exponentiation(base, exp);
                System.out.println("Result: " + formatResult(base) + " ^ "
                        + formatResult(exp) + " = " + formatResult(result));

            } else {
                System.out.println("[ERROR] Invalid choice.");
            }
        } catch (ArithmeticException e) {
            System.out.println("[MATH ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HANDLER: Unit Conversions
    // ═══════════════════════════════════════════════════════════════════

    private static void handleConversions(Scanner scanner) {
        System.out.println();
        System.out.println("--- Unit Conversions ---");
        System.out.println("1. Temperature");
        System.out.println("2. Currency");

        int choice = readMenuChoice(scanner, "Choose a conversion type (1-2): ");

        if (choice == 1) {
            handleTemperature(scanner);
        } else if (choice == 2) {
            handleCurrency(scanner);
        } else {
            System.out.println("[ERROR] Invalid choice.");
        }
    }

    private static void handleTemperature(Scanner scanner) {
        System.out.println();
        System.out.println("--- Temperature Conversions ---");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.println("3. Celsius to Kelvin");
        System.out.println("4. Kelvin to Celsius");

        int choice = readMenuChoice(scanner, "Choose (1-4): ");
        if (choice < 1 || choice > 4) {
            System.out.println("[ERROR] Invalid choice.");
            return;
        }

        BigDecimal temp = readBigDecimal(scanner, "Enter temperature: ");
        if (temp == null) return;

        BigDecimal result;
        switch (choice) {
            case 1:
                result = Conversions.celsiusToFahrenheit(temp);
                System.out.println(formatResult(temp) + " °C = " + formatResult(result) + " °F");
                break;
            case 2:
                result = Conversions.fahrenheitToCelsius(temp);
                System.out.println(formatResult(temp) + " °F = " + formatResult(result) + " °C");
                break;
            case 3:
                result = Conversions.celsiusToKelvin(temp);
                System.out.println(formatResult(temp) + " °C = " + formatResult(result) + " K");
                break;
            case 4:
                result = Conversions.kelvinToCelsius(temp);
                System.out.println(formatResult(temp) + " K = " + formatResult(result) + " °C");
                break;
            default:
                break;
        }
    }

    private static void handleCurrency(Scanner scanner) {
        System.out.println();
        System.out.println("--- Currency Conversions ---");
        System.out.println("Supported currencies: USD, EUR, GBP, INR");

        try {
            System.out.print("Enter 'from' currency code (e.g., USD): ");
            String from = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter 'to' currency code (e.g., EUR): ");
            String to = scanner.nextLine().trim().toUpperCase();

            BigDecimal amount = readBigDecimal(scanner, "Enter amount: ");
            if (amount == null) return;

            BigDecimal result = Conversions.convertCurrency(amount, from, to);
            System.out.println(formatResult(amount) + " " + from + " = "
                    + formatResult(result) + " " + to);
        } catch (IllegalArgumentException e) {
            System.out.println("[CONVERSION ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HANDLER: Expression Parser (BODMAS/PEMDAS)
    // ═══════════════════════════════════════════════════════════════════

    private static void handleExpression(Scanner scanner) {
        System.out.println();
        System.out.println("--- Expression Parser (BODMAS/PEMDAS) ---");
        System.out.println("Supports: +, -, *, /, ^ (power), sqrt()");
        System.out.println("Parentheses supported. Example: (5 + 3) * 2 ^ 3");
        System.out.print("Enter expression: ");

        String expr = scanner.nextLine().trim();
        if (expr.isEmpty()) {
            System.out.println("[ERROR] Empty expression.");
            return;
        }

        try {
            ExpressionParser parser = new ExpressionParser(expr);
            BigDecimal result = parser.parse();
            System.out.println("Result: " + expr + " = " + formatResult(result));
        } catch (ArithmeticException e) {
            System.out.println("[MATH ERROR] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[PARSE ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  INNER CLASS: Recursive Descent Expression Parser
    // ═══════════════════════════════════════════════════════════════════

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
     *   "5 + 3 * 2"        → 11  (not 16)
     *   "2 ^ 3 ^ 2"        → 512 (right-associative: 2^(3^2) = 2^9)
     *   "sqrt(16) + 1"     → 5
     *   "(2 + 3) * (4 - 1)" → 15
     */
    private static class ExpressionParser {

        private final String expr;
        private int pos;

        ExpressionParser(String expr) {
            this.expr = expr;
            this.pos = 0;
        }

        /**
         * Parses the entire expression and ensures all input is consumed.
         */
        BigDecimal parse() {
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
                if (op != '*' && op != '/') break;

                pos++; // consume operator
                BigDecimal right = parseFactor();

                if (op == '*') {
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

            // Handle number (digits, optional decimal point, optional leading sign handled by parseUnary)
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
}
