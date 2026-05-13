package com.example.CodeMan.service;

import com.example.CodeMan.dto.ReporteDtos.DesempenoPorCategoria;
import com.example.CodeMan.dto.ReporteDtos.ReporteData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerator {

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    public byte[] generar(ReporteData datos) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font bold   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                y = writeLine(cs, bold, 20, "Reporte de Desempeño — CodeMan", y, 0);
                y -= 8;
                y = writeLine(cs, normal, 12, "Estudiante: " + datos.nombreEstudiante(), y, 0);
                y = writeLine(cs, normal, 12, "Fecha: " + datos.fechaGeneracion()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), y, 0);
                y -= 16;

                y = writeLine(cs, bold, 14, "Resumen general", y, 0);
                y -= 4;
                y = writeLine(cs, normal, 12, "Partidas jugadas:  " + datos.totalPartidas(), y, 16);
                y = writeLine(cs, normal, 12, "Aciertos totales:  " + datos.aciertosTotal(), y, 16);
                y = writeLine(cs, normal, 12, "Errores totales:   " + datos.erroresTotal(), y, 16);
                y = writeLine(cs, normal, 12, "Rendimiento global: " + datos.porcentajeGeneral() + "%", y, 16);
                y -= 16;

                y = writeLine(cs, bold, 14, "Desempeño por categoría", y, 0);
                y -= 4;

                for (DesempenoPorCategoria cat : datos.porCategoria()) {
                    String line = String.format("%-20s  Aciertos: %3d   Errores: %3d   Rendimiento: %3d%%",
                            cat.categoria(), cat.aciertos(), cat.errores(), cat.porcentaje());
                    y = writeLine(cs, normal, 12, line, y, 16);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private float writeLine(PDPageContentStream cs, PDType1Font font, float size, String text, float y, float indent)
            throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(MARGIN + indent, y);
        cs.showText(text);
        cs.endText();
        return y - (size * 1.5f);
    }
}
