package com.studentmgmt.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javax.swing.*;
import java.awt.*;

/**
 * Analytics panel — embeds a JavaFX PieChart showing Pass vs Fail breakdown.
 * Uses JFXPanel as the Swing↔JavaFX bridge.
 *
 * Threading rules observed:
 * - Swing components created on the EDT (outer constructor).
 * - JavaFX Scene graph built inside Platform.runLater().
 */
public class ChartPanel extends JPanel {

    private final int passCount;
    private final int failCount;
    private final int totalCount;
    private final double avgGpa;

    public ChartPanel(int passCount, int failCount, int totalCount, double avgGpa) {
        this.passCount = passCount;
        this.failCount = failCount;
        this.totalCount = totalCount;
        this.avgGpa = avgGpa;

        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.BG_DARK);

        buildSwingHeader();
        buildJfxChart();
        buildSwingStatsFooter();
    }

    // ── Header (Swing) ───────────────────────────────────────────────

    private void buildSwingHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.BG_SURFACE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JLabel title = new JLabel("📊  Analytics — Pass vs Fail");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel sub = new JLabel(totalCount + " students enrolled  ·  Avg GPA: " + String.format("%.2f", avgGpa));
        sub.setFont(ThemeManager.FONT_SMALL);
        sub.setForeground(ThemeManager.TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
    }

    // ── JavaFX chart (JFXPanel) ──────────────────────────────────────

    private void buildJfxChart() {
        // Prevent JavaFX runtime from exiting when charts are removed
        Platform.setImplicitExit(false);

        JFXPanel fxPanel = new JFXPanel();
        fxPanel.setBackground(ThemeManager.BG_DARK);
        add(fxPanel, BorderLayout.CENTER);

        // Build the JavaFX scene on the JavaFX Application Thread
        Platform.runLater(() -> {
            PieChart.Data passSlice = new PieChart.Data("Pass (" + passCount + ")", passCount);
            PieChart.Data failSlice = new PieChart.Data("Fail (" + failCount + ")", failCount);

            PieChart chart = new PieChart(FXCollections.observableArrayList(passSlice, failSlice));
            chart.setTitle("Student Performance");
            chart.setAnimated(true);
            chart.setLabelsVisible(true);
            chart.setLegendVisible(true);
            chart.setStartAngle(90);

            // Dark background via CSS
            BorderPane root = new BorderPane(chart);
            root.setStyle("-fx-background-color: #0d1117;");
            chart.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pie-label-visible: true;");

            // Color slices after layout
            chart.applyCss();
            chart.layout();

            // Style each slice via lookup — set after adding to scene
            Scene scene = new Scene(root, Color.web("#0d1117"));

            // Apply colors after the chart is in the scene
            root.sceneProperty().addListener((obs, o, n) -> {
                if (n != null) {
                    Platform.runLater(() -> {
                        try {
                            // Green for Pass
                            passSlice.getNode().setStyle("-fx-pie-color: #3fb950;");
                            // Red for Fail
                            failSlice.getNode().setStyle("-fx-pie-color: #f85149;");
                        } catch (Exception ignored) {
                        }
                    });
                }
            });

            // Tooltip on hover
            for (PieChart.Data data : chart.getData()) {
                double pct = totalCount > 0 ? (data.getPieValue() / totalCount) * 100 : 0;
                String label = String.format("%.1f%%", pct);
                javafx.scene.control.Tooltip tp = new javafx.scene.control.Tooltip(
                        data.getName() + "\n" + (int) data.getPieValue() + " students (" + label + ")");
                javafx.scene.control.Tooltip.install(data.getNode(), tp);
            }

            fxPanel.setScene(scene);
        });
    }

    // ── Stats footer (Swing) ─────────────────────────────────────────

    private void buildSwingStatsFooter() {
        JPanel footer = new JPanel(new GridLayout(1, 4, 16, 0));
        footer.setBackground(ThemeManager.BG_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        double passRate = totalCount > 0 ? (passCount * 100.0 / totalCount) : 0;
        double failRate = 100 - passRate;

        footer.add(buildStatCard("Total Students", String.valueOf(totalCount), ThemeManager.ACCENT_BLUE));
        footer.add(buildStatCard("Passing", passCount + " (" + String.format("%.0f%%", passRate) + ")",
                ThemeManager.ACCENT_GREEN));
        footer.add(buildStatCard("Failing", failCount + " (" + String.format("%.0f%%", failRate) + ")",
                ThemeManager.ACCENT_RED));
        footer.add(buildStatCard("Avg GPA", String.format("%.2f / 4.00", avgGpa), ThemeManager.ACCENT_PURPLE));

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildStatCard(String label, String value, java.awt.Color color) {
        JPanel card = ThemeManager.createCardPanel();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel lbl = ThemeManager.createFormLabel(label);
        JLabel val = new JLabel(value);
//        val.setFont(new Font("Consolas", Font.BOLD, 22));
        val.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 22));
        val.setForeground(color);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }
}
