package ru.marilka.swotbackend.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.entity.SessionEntity;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.model.entity.SwotVersionEntity;
import ru.marilka.swotbackend.repository.AlternativeRepository;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.SessionRepository;
import ru.marilka.swotbackend.repository.VersionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final VersionRepository versionRepository;
    private final FactorRepository factorRepository;
    private final AlternativeService alternativeService;

    public void markSessionAsCompleted(Long sessionId) {
        var session = sessionRepository.findById(sessionId).orElseThrow();
        session.setCompleted(true);
        session.setLastModified(LocalDateTime.now());
        sessionRepository.save(session);
    }


    public SwotVersionEntity createNewVersion(Long sessionId) {
       SwotVersionEntity version = new SwotVersionEntity();
        version.setSessionId(sessionId);
        version.setCreatedAt(LocalDateTime.now());

        return versionRepository.save(version);
    }

    public void completeLastSession() {
        SessionEntity session = sessionRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Сессия не найдена"));

        session.setCompleted(true);
        session.setLastModified(LocalDateTime.now());

        sessionRepository.save(session);
    }

    public Optional<SessionEntity> getSession(Long id) {
        return sessionRepository.findById(id);
    }

    public List<SessionEntity> getUserSessions() {
        return sessionRepository.findAllByAdminId(1L);
    }

    public SessionEntity create(String name, String userId) {
        SessionEntity session = new SessionEntity();
        session.setName(name);
        return sessionRepository.save(session);
    }
    public byte[] exportToPdf(List<SwotFactorEntity> factors, List<AlternativeDto> alternatives) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            document.add(new Paragraph("Результаты сессии").setFont(boldFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Факторы (группировка по типу)
            Map<String, List<SwotFactorEntity>> groupedFactors = factors.stream()
                    .collect(groupingBy(SwotFactorEntity::getType));

            for (String type : List.of("strong", "weak", "opportunity", "threat")) {
                List<SwotFactorEntity> typedFactors = groupedFactors.getOrDefault(type, Collections.emptyList());

                if (!typedFactors.isEmpty()) {
                    document.add(new Paragraph(getTypeName(type)).setFont(boldFont).setFontSize(14));

                    Table factorTable = new Table(new float[]{1, 10}).useAllAvailableWidth();
                    factorTable.addHeaderCell(new Cell().add(new Paragraph("№").setFont(boldFont)));
                    factorTable.addHeaderCell(new Cell().add(new Paragraph("Наименование").setFont(boldFont)));

                    for (int i = 0; i < typedFactors.size(); i++) {
                        SwotFactorEntity factor = typedFactors.get(i);
                        factorTable.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1)).setFont(font)));
                        factorTable.addCell(new Cell().add(new Paragraph(factor.getTitle()).setFont(font)));
                    }

                    document.add(factorTable);
                    document.add(new Paragraph("\n"));
                }
            }

            // Альтернативы
            document.add(new Paragraph("Альтернативы").setFont(boldFont).setFontSize(14));

            Table altTable = new Table(new float[]{1, 5, 2, 2, 2}).useAllAvailableWidth();
            altTable.addHeaderCell(new Cell().add(new Paragraph("A№").setFont(boldFont)));
            altTable.addHeaderCell(new Cell().add(new Paragraph("Описание").setFont(boldFont)));
            altTable.addHeaderCell(new Cell().add(new Paragraph("d+").setFont(boldFont)));
            altTable.addHeaderCell(new Cell().add(new Paragraph("d-").setFont(boldFont)));
            altTable.addHeaderCell(new Cell().add(new Paragraph("d*").setFont(boldFont)));

            for (int i = 0; i < alternatives.size(); i++) {
                AlternativeDto alt = alternatives.get(i);
                altTable.addCell(new Cell().add(new Paragraph("A" + (i + 1)).setFont(font)));
                altTable.addCell(new Cell().add(new Paragraph(alt.getInternalFactor() + " и " + alt.getExternalFactor()).setFont(font)));
                altTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", alt.getDPlus())).setFont(font)));
                altTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", alt.getDMinus())).setFont(font)));
                altTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", alt.getCloseness())).setFont(font)));
            }

            document.add(altTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "strong" -> "Сильные стороны";
            case "weak" -> "Слабые стороны";
            case "opportunity" -> "Возможности";
            case "threat" -> "Угрозы";
            default -> type;
        };
    }

    public byte[] exportFullResultsToPdf(Long sessionId, Long versionId) {
        List<SwotFactorEntity> factors = factorRepository.findBySessionIdAndVersionId(sessionId, versionId);
        List<SwotAlternativeEntity> alternatives = alternativeService.getBySessionAndVersion(sessionId, versionId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        // Заголовок
        doc.add(new Paragraph("Результаты SWOT-анализа")
                .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("Дата выгрузки: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .setFontSize(10).setTextAlignment(TextAlignment.RIGHT));

        doc.add(new Paragraph("\nФАКТОРЫ").setBold().setFontSize(14));

        Map<String, List<SwotFactorEntity>> grouped = factors.stream()
                .collect(groupingBy(SwotFactorEntity::getType));

        addFactorTable(doc, "Сильные стороны", grouped.get("strong"));
        addFactorTable(doc, "Слабые стороны", grouped.get("weak"));
        addFactorTable(doc, "Возможности", grouped.get("opportunity"));
        addFactorTable(doc, "Угрозы", grouped.get("threat"));

        doc.add(new Paragraph("\nАЛЬТЕРНАТИВЫ").setBold().setFontSize(14));
        addAlternativesTable(doc, alternatives);

        doc.close();
        return out.toByteArray();
    }

    private void addFactorTable(Document doc, String title, List<SwotFactorEntity> list) {
        if (list == null || list.isEmpty()) return;

        doc.add(new Paragraph(title).setBold().setMarginTop(10f));

        Table table = new Table(new float[]{1, 8})
                .useAllAvailableWidth();

        table.addHeaderCell(createHeaderCell("№"));
        table.addHeaderCell(createHeaderCell("Название"));

        for (int i = 0; i < list.size(); i++) {
            SwotFactorEntity f = list.get(i);
            table.addCell(createBodyCell(String.valueOf(i + 1)));
            table.addCell(createBodyCell(f.getTitle()));
        }

        doc.add(table);
    }

    private void addAlternativesTable(Document doc, List<SwotAlternativeEntity> alternatives) {
        Table table = new Table(new float[]{1, 2, 2, 2, 5})
                .useAllAvailableWidth();

        table.addHeaderCell(createHeaderCell("№"));
        table.addHeaderCell(createHeaderCell("d+"));
        table.addHeaderCell(createHeaderCell("d-"));
        table.addHeaderCell(createHeaderCell("d*"));
        table.addHeaderCell(createHeaderCell("Факторы"));

        for (int i = 0; i < alternatives.size(); i++) {
            SwotAlternativeEntity a = alternatives.get(i);
            table.addCell(createBodyCell("A" + (i + 1)));
            table.addCell(createBodyCell(format(a.getDPlus())));
            table.addCell(createBodyCell(format(a.getDMinus())));
            table.addCell(createBodyCell(format(a.getCloseness())));
            table.addCell(createBodyCell(a.getInternalFactor() + " и " + a.getExternalFactor()));
        }

        doc.add(table);
    }

    private String format(Double val) {
        return val == null ? "-" : String.format("%.3f", val);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(1));
    }

    private Cell createBodyCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(0.5f));
    }



}

