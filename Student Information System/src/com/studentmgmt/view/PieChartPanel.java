package com.studentmgmt.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Custom Swing panel that paints a Pie Chart for Pass vs Fail visualization.
 * Uses Java2D — no external charting library required.
 */
public class PieChartPanel extends JPanel {

    private int passCount = 0;
    private int failCount = 0;

    private static final Color PASS_COLOR = new Color(63, 185, 80);
    private static final Color FAIL_COLOR = new Color(248, 81, 73);
    private static final Color PASS_HOVER = new Color(83, 205, 100);
    private static final Color FAIL_HOVER = new Color(255, 111, 103);
    private static final Color EMPTY_COLOR = new Color(48, 54, 61);

    private int hoveredSlice = -1; // 0=pass, 1=fail

    public PieChartPanel() {
        setBackground(ThemeManager.BG_SURFACE);
        setPreferredSize(new Dimension(600, 500));

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int total = passCount + failCount;
                if (total == 0) { hoveredSlice = -1; repaint(); return; }

                // Check if mouse is inside the pie
                int cx = getWidth() / 2;
                int cy = getHeight() / 2 - 10;
                int radius = Math.min(getWidth(), getHeight()) / 3;
                double dx = e.getX() - cx;
                double dy = e.getY() - cy;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > radius) { hoveredSlice = -1; repaint(); return; }

                double angle = Math.toDegrees(Math.atan2(-dy, dx));
                if (angle < 0) angle += 360;

                double passAngle = 360.0 * passCount / total;
                hoveredSlice = (angle <= passAngle) ? 0 : 1;
                repaint();
            }
        });
    }

    public void updateData(int pass, int fail) {
        this.passCount = pass;
        this.failCount = fail;
        hoveredSlice = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        // Title
        g2.setFont(ThemeManager.FONT_TITLE);
        g2.setColor(ThemeManager.TEXT_PRIMARY);
        String title = "\uD83D\uDCCA  Pass vs Fail Distribution";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, 40);

        int total = passCount + failCount;
        int cx = w / 2;
        int cy = h / 2 - 10;
        int radius = Math.min(w, h) / 3;
        int diameter = radius * 2;
        int x = cx - radius;
        int y = cy - radius;

        if (total == 0) {
            // Empty state
            g2.setColor(EMPTY_COLOR);
            g2.fillOval(x, y, diameter, diameter);
            g2.setFont(ThemeManager.FONT_SUBTITLE);
            g2.setColor(ThemeManager.TEXT_MUTED);
            String msg = "No student data yet";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, cy + 5);
            g2.dispose();
            return;
        }

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(x + 4, y + 4, diameter, diameter);

        // Draw pie slices
        double passAngle = 360.0 * passCount / total;
        double failAngle = 360.0 - passAngle;

        // Pass slice
        int passExplode = (hoveredSlice == 0) ? 6 : 0;
        double passMid = Math.toRadians(passAngle / 2);
        g2.setColor(hoveredSlice == 0 ? PASS_HOVER : PASS_COLOR);
        g2.fill(new Arc2D.Double(x + passExplode * Math.cos(passMid),
                y - passExplode * Math.sin(passMid),
                diameter, diameter, 0, passAngle, Arc2D.PIE));

        // Fail slice
        int failExplode = (hoveredSlice == 1) ? 6 : 0;
        double failMid = Math.toRadians(passAngle + failAngle / 2);
        g2.setColor(hoveredSlice == 1 ? FAIL_HOVER : FAIL_COLOR);
        g2.fill(new Arc2D.Double(x + failExplode * Math.cos(failMid),
                y - failExplode * Math.sin(failMid),
                diameter, diameter, passAngle, failAngle, Arc2D.PIE));

        // Center donut hole (modern look)
        int innerR = radius / 2;
        g2.setColor(ThemeManager.BG_SURFACE);
        g2.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // Center text
        g2.setFont(new Font("Consolas", Font.BOLD, 28));
        g2.setColor(ThemeManager.TEXT_PRIMARY);
        String totalStr = String.valueOf(total);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(totalStr, cx - fm.stringWidth(totalStr) / 2, cy + 4);
        g2.setFont(ThemeManager.FONT_SMALL);
        g2.setColor(ThemeManager.TEXT_MUTED);
        String sub = "students";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(sub, cx - fm2.stringWidth(sub) / 2, cy + 20);

        // Legend
        int legendY = h - 80;
        int legendX = cx - 120;

        drawLegendItem(g2, legendX, legendY, PASS_COLOR,
                String.format("Pass: %d (%.1f%%)", passCount, 100.0 * passCount / total));
        drawLegendItem(g2, legendX, legendY + 30, FAIL_COLOR,
                String.format("Fail: %d (%.1f%%)", failCount, 100.0 * failCount / total));

        g2.dispose();
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, 20, 20, 6, 6);
        g2.setFont(ThemeManager.FONT_BODY);
        g2.setColor(ThemeManager.TEXT_PRIMARY);
        g2.drawString(text, x + 30, y + 15);
    }
}
