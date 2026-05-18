package com.studentmgmt.util;

import com.studentmgmt.model.Student;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports student records to a professionally formatted PDF using iText 7.
 */
public class PdfExporter {

    private static final DeviceRgb HEADER_BG    = new DeviceRgb(26, 42, 74);
    private static final DeviceRgb ROW_ALT_BG   = new DeviceRgb(240, 244, 248);
    private static final DeviceRgb PASS_COLOR   = new DeviceRgb(39, 174, 96);
    private static final DeviceRgb FAIL_COLOR   = new DeviceRgb(231, 76, 60);
    private static final DeviceRgb ACCENT       = new DeviceRgb(52, 152, 219);

    public static void export(List<Student> students, File outputFile) throws Exception {
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, PageSize.A4.rotate()); // landscape
        doc.setMargins(30, 30, 30, 30);

        PdfFont bold    = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regular = PdfFontFactory.createFont("Helvetica");

        // ── Title ───────────────────────────────────────────────────
        doc.add(new Paragraph("Student Information System — Report")
                .setFont(bold).setFontSize(20)
                .setFontColor(HEADER_BG)
                .setMarginBottom(4));

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        doc.add(new Paragraph("Generated: " + timestamp)
                .setFont(regular).setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setMarginBottom(16));

        // ── Summary stats ───────────────────────────────────────────
        long pass = students.stream().filter(Student::isPassing).count();
        long fail = students.size() - pass;
        double avgGpa = students.isEmpty() ? 0 :
                students.stream().mapToDouble(Student::getGpa).average().orElse(0);

        doc.add(new Paragraph(String.format(
                "Total Students: %d    |    Pass: %d    |    Fail: %d    |    Average GPA: %.2f",
                students.size(), pass, fail, avgGpa))
                .setFont(bold).setFontSize(10)
                .setFontColor(ACCENT)
                .setMarginBottom(14));

        // ── Table ───────────────────────────────────────────────────
        String[] headers = {
            "ID", "Name", "Age", "DOB", "Gender", "Mobile", "Email",
            "Course", "Math", "Sci", "Eng", "Hist", "CS", "Avg", "GPA", "Grade", "Status"
        };
        float[] colWidths = {28, 80, 25, 58, 48, 65, 100, 68, 28, 28, 28, 28, 28, 32, 30, 35, 38};

        Table table = new Table(UnitValue.createPointArray(colWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Header row
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(bold).setFontSize(7)
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_BG)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4)
                    .setBorder(new SolidBorder(ColorConstants.WHITE, 0.5f)));
        }

        // Data rows
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            DeviceRgb rowBg = (i % 2 == 0) ? null : ROW_ALT_BG;
            double[] marks = s.getMarks();

            String[] values = {
                    String.valueOf(s.getId()),
                    s.getName(),
                    String.valueOf(s.getAge()),
                    s.getDob().isEmpty()    ? "—" : s.getDob(),
                    s.getGender().isEmpty() ? "—" : s.getGender(),
                    s.getMobile().isEmpty() ? "—" : s.getMobile(),
                    s.getEmail().isEmpty()  ? "—" : s.getEmail(),
                    s.getCourse(),
                    String.format("%.0f", marks[0]),
                    String.format("%.0f", marks[1]),
                    String.format("%.0f", marks[2]),
                    String.format("%.0f", marks[3]),
                    String.format("%.0f", marks[4]),
                    String.format("%.1f", s.getAverageMarks()),
                    String.format("%.2f", s.getGpa()),
                    s.getLetterGrade(),
                    s.getStatus()
            };

            for (int j = 0; j < values.length; j++) {
                Cell cell = new Cell()
                        .add(new Paragraph(values[j]).setFont(regular).setFontSize(7))
                        .setTextAlignment(j == 1 || j == 4 || j == 5 || j == 6 || j == 7
                                ? TextAlignment.LEFT : TextAlignment.CENTER)
                        .setPadding(4)
                        .setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 0.5f));

                if (rowBg != null) cell.setBackgroundColor(rowBg);

                // Color-code status column
                if (j == values.length - 1) {
                    cell.setFontColor(s.isPassing() ? PASS_COLOR : FAIL_COLOR);
                    cell.setFont(bold);
                }
                table.addCell(cell);
            }
        }

        doc.add(table);
        doc.close();
    }
}
