package com.student.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView displayExpression, displayResult;
    private LinearLayout scientificRow1, scientificRow2;
    private ImageButton btnToggleMode, btnHistory;
    private boolean isScientificMode = false;
    
    private StringBuilder expression = new StringBuilder();
    private DecimalFormat df = new DecimalFormat("#.##########");
    
    // History
    private ArrayList<String> historyList = new ArrayList<>();

    // Button IDs
    private int[] numberButtons = {
        R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayExpression = findViewById(R.id.displayExpression);
        displayResult = findViewById(R.id.displayResult);
        scientificRow1 = findViewById(R.id.scientificRow1);
        scientificRow2 = findViewById(R.id.scientificRow2);
        btnToggleMode = findViewById(R.id.btnToggleMode);
        btnHistory = findViewById(R.id.btnHistory);

        // Hide scientific rows by default
        scientificRow1.setVisibility(View.GONE);
        scientificRow2.setVisibility(View.GONE);

        // Set click listeners for number buttons
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(this);
        }

        // Set click listeners for all other buttons
        int[] otherButtons = {
            R.id.btnDot, R.id.btnEquals, R.id.btnPlus, R.id.btnMinus,
            R.id.btnMultiply, R.id.btnDivide, R.id.btnClear, R.id.btnBackspace,
            R.id.btnPercent, R.id.btnPlusMinus, R.id.btnSqrt, R.id.btnSquare,
            R.id.btnPower, R.id.btnReciprocal, R.id.btnSin, R.id.btnCos,
            R.id.btnTan, R.id.btnLog, R.id.btnPi, R.id.btnE
        };

        for (int id : otherButtons) {
            findViewById(id).setOnClickListener(this);
        }

        // Toggle mode button
        btnToggleMode.setOnClickListener(v -> toggleScientificMode());
        
        // History button
        btnHistory.setOnClickListener(v -> showHistory());
    }

    private void toggleScientificMode() {
        isScientificMode = !isScientificMode;
        if (isScientificMode) {
            scientificRow1.setVisibility(View.VISIBLE);
            scientificRow2.setVisibility(View.VISIBLE);
            btnToggleMode.setImageResource(R.drawable.ic_scientific_on);
        } else {
            scientificRow1.setVisibility(View.GONE);
            scientificRow2.setVisibility(View.GONE);
            btnToggleMode.setImageResource(R.drawable.ic_scientific_off);
        }
    }

    private void showHistory() {
        if (historyList.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("History")
                .setMessage("No calculations yet")
                .setPositiveButton("OK", null)
                .show();
            return;
        }

        String[] historyArray = historyList.toArray(new String[0]);
        new AlertDialog.Builder(this)
            .setTitle("History")
            .setItems(historyArray, (dialog, which) -> {
                String item = historyArray[which];
                String[] parts = item.split("=");
                if (parts.length > 1) {
                    expression = new StringBuilder(parts[1].trim());
                    updateDisplay();
                }
            })
            .setNegativeButton("Clear", (dialog, which) -> historyList.clear())
            .setPositiveButton("Close", null)
            .show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        try {
            // Number buttons
            for (int i = 0; i < numberButtons.length; i++) {
                if (id == numberButtons[i]) {
                    appendToExpression(String.valueOf(i));
                    return;
                }
            }

            // Other buttons
            if (id == R.id.btnDot) {
                appendToExpression(".");
            } else if (id == R.id.btnPlus) {
                appendOperator("+");
            } else if (id == R.id.btnMinus) {
                appendOperator("−");
            } else if (id == R.id.btnMultiply) {
                appendOperator("×");
            } else if (id == R.id.btnDivide) {
                appendOperator("÷");
            } else if (id == R.id.btnEquals) {
                calculateFinal();
            } else if (id == R.id.btnClear) {
                clearAll();
            } else if (id == R.id.btnBackspace) {
                handleBackspace();
            } else if (id == R.id.btnPercent) {
                appendOperator("%");
            } else if (id == R.id.btnPlusMinus) {
                toggleSign();
            } else if (id == R.id.btnSqrt) {
                appendFunction("√(");
            } else if (id == R.id.btnSquare) {
                appendToExpression("²");
            } else if (id == R.id.btnPower) {
                appendOperator("^");
            } else if (id == R.id.btnReciprocal) {
                handleReciprocal();
            } else if (id == R.id.btnSin) {
                appendFunction("sin(");
            } else if (id == R.id.btnCos) {
                appendFunction("cos(");
            } else if (id == R.id.btnTan) {
                appendFunction("tan(");
            } else if (id == R.id.btnLog) {
                appendFunction("log(");
            } else if (id == R.id.btnPi) {
                appendToExpression("π");
            } else if (id == R.id.btnE) {
                appendToExpression("e");
            }
        } catch (Exception e) {
            displayResult.setText("Error");
        }
    }

    private void appendToExpression(String str) {
        expression.append(str);
        updateDisplay();
    }

    private void appendOperator(String op) {
        if (expression.length() > 0) {
            char last = expression.charAt(expression.length() - 1);
            // Replace operator if last char is an operator
            if (isOperator(last)) {
                expression.setLength(expression.length() - 1);
            }
        }
        expression.append(op);
        updateDisplay();
    }

    private void appendFunction(String func) {
        expression.append(func);
        updateDisplay();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '−' || c == '×' || c == '÷' || c == '^' || c == '%';
    }

    private void updateDisplay() {
        String expr = expression.toString();
        displayExpression.setText(expr.isEmpty() ? "0" : expr);
        
        // Only show preview if expression contains an operator (actual calculation)
        boolean hasOperator = containsOperator(expr);
        
        if (!hasOperator) {
            displayResult.setText("");
            return;
        }
        
        // Check if expression ends with operator (incomplete calculation)
        if (expr.length() > 0 && isOperator(expr.charAt(expr.length() - 1))) {
            displayResult.setText("");
            return;
        }
        
        // Try to calculate preview
        try {
            double result = evaluateExpression(expr);
            if (!Double.isNaN(result) && !Double.isInfinite(result)) {
                displayResult.setText("= " + df.format(result));
            } else {
                displayResult.setText("");
            }
        } catch (Exception e) {
            displayResult.setText("");
        }
    }
    
    private boolean containsOperator(String expr) {
        // Check if expression contains any operator
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            // Skip if it's a negative sign at the start
            if (i == 0 && c == '-') continue;
            if (c == '+' || c == '−' || c == '×' || c == '÷' || c == '^' || c == '%') {
                return true;
            }
        }
        return false;
    }

    private double evaluateExpression(String expr) {
        if (expr.isEmpty()) return Double.NaN;
        
        // Replace symbols with calculable values
        expr = expr.replace("π", String.valueOf(Math.PI));
        expr = expr.replace("e", String.valueOf(Math.E));
        expr = expr.replace("−", "-");
        expr = expr.replace("×", "*");
        expr = expr.replace("÷", "/");
        
        // Handle square (²)
        while (expr.contains("²")) {
            int idx = expr.indexOf("²");
            int start = idx - 1;
            while (start > 0 && (Character.isDigit(expr.charAt(start - 1)) || expr.charAt(start - 1) == '.')) {
                start--;
            }
            String numStr = expr.substring(start, idx);
            double num = Double.parseDouble(numStr);
            expr = expr.substring(0, start) + String.valueOf(num * num) + expr.substring(idx + 1);
        }
        
        // Handle functions
        expr = handleFunction(expr, "sin", true);
        expr = handleFunction(expr, "cos", true);
        expr = handleFunction(expr, "tan", true);
        expr = handleFunction(expr, "log", false);
        expr = handleFunction(expr, "√", false);
        
        // Handle percentage
        while (expr.contains("%")) {
            int idx = expr.indexOf("%");
            int start = idx - 1;
            while (start > 0 && (Character.isDigit(expr.charAt(start - 1)) || expr.charAt(start - 1) == '.')) {
                start--;
            }
            String numStr = expr.substring(start, idx);
            double num = Double.parseDouble(numStr);
            expr = expr.substring(0, start) + String.valueOf(num / 100) + expr.substring(idx + 1);
        }
        
        // Handle power (^)
        while (expr.contains("^")) {
            int idx = expr.indexOf("^");
            // Find number before ^
            int start = idx - 1;
            while (start > 0 && (Character.isDigit(expr.charAt(start - 1)) || expr.charAt(start - 1) == '.' || expr.charAt(start - 1) == '-')) {
                start--;
            }
            // Find number after ^
            int end = idx + 1;
            if (end < expr.length() && expr.charAt(end) == '-') end++;
            while (end < expr.length() && (Character.isDigit(expr.charAt(end)) || expr.charAt(end) == '.')) {
                end++;
            }
            String num1Str = expr.substring(start, idx);
            String num2Str = expr.substring(idx + 1, end);
            double result = Math.pow(Double.parseDouble(num1Str), Double.parseDouble(num2Str));
            expr = expr.substring(0, start) + result + expr.substring(end);
        }
        
        return evaluateBasicExpression(expr);
    }

    private String handleFunction(String expr, String funcName, boolean isDegrees) {
        while (expr.contains(funcName + "(")) {
            int funcStart = expr.indexOf(funcName + "(");
            int parenStart = funcStart + funcName.length();
            int parenEnd = findClosingParen(expr, parenStart);
            if (parenEnd == -1) parenEnd = expr.length();
            
            String inner = expr.substring(parenStart + 1, parenEnd);
            double innerVal;
            try {
                innerVal = evaluateBasicExpression(inner);
            } catch (Exception e) {
                innerVal = Double.parseDouble(inner);
            }
            
            double result;
            switch (funcName) {
                case "sin":
                    result = Math.sin(Math.toRadians(innerVal));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(innerVal));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(innerVal));
                    break;
                case "log":
                    result = Math.log10(innerVal);
                    break;
                case "√":
                    result = Math.sqrt(innerVal);
                    break;
                default:
                    result = innerVal;
            }
            
            expr = expr.substring(0, funcStart) + result + (parenEnd < expr.length() ? expr.substring(parenEnd + 1) : "");
        }
        return expr;
    }

    private int findClosingParen(String expr, int openIndex) {
        int count = 1;
        for (int i = openIndex + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            if (expr.charAt(i) == ')') count--;
            if (count == 0) return i;
        }
        return -1;
    }

    private double evaluateBasicExpression(String expr) {
        // Simple expression evaluator for +, -, *, /
        expr = expr.trim();
        if (expr.isEmpty()) return Double.NaN;
        
        // Handle negative at start
        if (expr.startsWith("--")) {
            expr = expr.substring(2);
        }
        
        // Split by + and - (keeping operators)
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();
        
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if ((c == '+' || c == '-') && i > 0 && expr.charAt(i-1) != '*' && expr.charAt(i-1) != '/') {
                if (current.length() > 0) {
                    numbers.add(evaluateMultDiv(current.toString()));
                    current = new StringBuilder();
                }
                operators.add(c);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            numbers.add(evaluateMultDiv(current.toString()));
        }
        
        if (numbers.isEmpty()) return Double.NaN;
        
        double result = numbers.get(0);
        for (int i = 0; i < operators.size() && i + 1 < numbers.size(); i++) {
            if (operators.get(i) == '+') {
                result += numbers.get(i + 1);
            } else {
                result -= numbers.get(i + 1);
            }
        }
        
        return result;
    }

    private double evaluateMultDiv(String expr) {
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();
        
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '*' || c == '/') {
                if (current.length() > 0) {
                    numbers.add(Double.parseDouble(current.toString()));
                    current = new StringBuilder();
                }
                operators.add(c);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            numbers.add(Double.parseDouble(current.toString()));
        }
        
        if (numbers.isEmpty()) return 0;
        
        double result = numbers.get(0);
        for (int i = 0; i < operators.size() && i + 1 < numbers.size(); i++) {
            if (operators.get(i) == '*') {
                result *= numbers.get(i + 1);
            } else {
                result /= numbers.get(i + 1);
            }
        }
        
        return result;
    }

    private void calculateFinal() {
        String expr = expression.toString();
        try {
            double result = evaluateExpression(expr);
            if (!Double.isNaN(result) && !Double.isInfinite(result)) {
                String historyEntry = expr + " = " + df.format(result);
                historyList.add(0, historyEntry);
                if (historyList.size() > 50) historyList.remove(historyList.size() - 1);
                
                expression = new StringBuilder(df.format(result));
                displayExpression.setText(df.format(result));
                displayResult.setText("");
            }
        } catch (Exception e) {
            displayResult.setText("Error");
        }
    }

    private void clearAll() {
        expression = new StringBuilder();
        displayExpression.setText("0");
        displayResult.setText("");
    }

    private void handleBackspace() {
        if (expression.length() > 0) {
            expression.setLength(expression.length() - 1);
            updateDisplay();
        }
        if (expression.length() == 0) {
            displayExpression.setText("0");
            displayResult.setText("");
        }
    }

    private void toggleSign() {
        String expr = expression.toString();
        if (expr.startsWith("-")) {
            expression = new StringBuilder(expr.substring(1));
        } else {
            expression = new StringBuilder("-" + expr);
        }
        updateDisplay();
    }

    private void handleReciprocal() {
        try {
            double val = evaluateExpression(expression.toString());
            if (val != 0) {
                expression = new StringBuilder(df.format(1 / val));
                updateDisplay();
            }
        } catch (Exception e) {
            displayResult.setText("Error");
        }
    }
}
