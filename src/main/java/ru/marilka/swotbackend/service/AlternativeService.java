package ru.marilka.swotbackend.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marilka.swotbackend.model.AlternativeDto;
import ru.marilka.swotbackend.model.Factor;
import ru.marilka.swotbackend.model.entity.SwotAlternativeEntity;
import ru.marilka.swotbackend.model.entity.SwotFactorEntity;
import ru.marilka.swotbackend.repository.AlternativeRepository;
import ru.marilka.swotbackend.repository.FactorRepository;
import ru.marilka.swotbackend.repository.UserSessionRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlternativeService {
    private final FactorRepository factorRepository;
    private final AlternativeRepository alternativeRepository;
    private final UserSessionRepository userSessionRepository;

    private static final List<Double> ALPHA_LEVELS = List.of(0.1, 0.5, 0.9);

    public byte[] exportToPdf(String sessionName, List<Factor> factors, List<AlternativeDto> alternatives) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("C:\\Users\\Admin\\IdeaProjects\\swot-backend\\src\\main\\resources\\fonts\\Roboto-Italic-VariableFont_wdth,wght.ttf", PdfEncodings.IDENTITY_H);
        doc.setFont(font);

        doc.add(new Paragraph(sessionName).setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));

        addFactorTable(doc, "Сильные стороны", factors, "strong", ColorConstants.GREEN);
        addFactorTable(doc, "Слабые стороны", factors, "weak", ColorConstants.RED);
        addFactorTable(doc, "Возможности", factors, "opportunity", ColorConstants.BLUE);
        addFactorTable(doc, "Угрозы", factors, "threat", ColorConstants.BLACK);

        doc.add(new AreaBreak());
        doc.add(new Paragraph("АЛЬТЕРНАТИВЫ").setFontSize(16).setBold());

        Table altTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 5}))
                .setWidth(UnitValue.createPercentValue(100));
        altTable.addHeaderCell("ID");
        altTable.addHeaderCell("d+");
        altTable.addHeaderCell("d-");
        altTable.addHeaderCell("d*");
        altTable.addHeaderCell("Описание");

        int index = 1;
        for (AlternativeDto alt : alternatives) {
            altTable.addCell("A" + index++);
            altTable.addCell(formatDouble(alt.getDPlus()));
            altTable.addCell(formatDouble(alt.getDMinus()));
            altTable.addCell(formatDouble(alt.getCloseness()));
            altTable.addCell(alt.getInternalFactor() + " и " + alt.getExternalFactor());
        }
        doc.add(altTable);
        doc.close();
        return baos.toByteArray();
    }

    private void addFactorTable(Document doc, String title, List<Factor> factors, String type, com.itextpdf.kernel.colors.Color color) {
        doc.add(new Paragraph(title).setFontSize(14).setBold().setFontColor(color));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5}))
                .setWidth(UnitValue.createPercentValue(100));
        table.addHeaderCell("Центр массы");
        table.addHeaderCell("Фактор");

        factors.stream()
                .filter(f -> f.getType().equalsIgnoreCase(type))
                .forEach(f -> {
                    table.addCell(formatDouble(f.getMassCenter()));
                    table.addCell(f.getName());
                });

        doc.add(table);
    }

    private String formatDouble(Double value) {
        return value != null ? String.format("%.3f", value) : "-";
    }

    public List<AlternativeDto> calculateSelectedAlternatives(List<Long> selectedIds) {
        List<SwotFactorEntity> selectedFactors = factorRepository.findAllById(selectedIds)
                .stream()
                .peek(f -> f.setSelected(true))
                .toList();
        factorRepository.saveAll(selectedFactors);
        return calculateAlternativesFromFactors(selectedFactors);
    }

    @Transactional
    public List<AlternativeDto> calculateAlternatives(Long sessionId, Long versionId) {
        List<SwotFactorEntity> factors = factorRepository.findAllBySessionIdAndVersionId(sessionId, versionId)
                .stream()
                .peek(f -> f.setSelected(true))
                .toList();
        factorRepository.saveAll(factors);
        List<AlternativeDto> result = calculateAlternativesFromFactors(factors);
        alternativeRepository.deleteBySessionIdAndVersionId(sessionId, versionId);
        alternativeRepository.saveAll(toEntities(result, sessionId, versionId));
        return result;
    }

    @Transactional
    public List<AlternativeDto> calculateSelectedAlternatives(Long sessionId, Long versionId) {
        List<SwotFactorEntity> selectedFactors = factorRepository.findAllBySessionIdAndVersionId(sessionId, versionId)
                .stream()
                .filter(SwotFactorEntity::isSelected)
                .toList();
        List<AlternativeDto> result = calculateAlternativesFromFactors(selectedFactors);
        alternativeRepository.deleteBySessionIdAndVersionId(sessionId, versionId);
        alternativeRepository.saveAll(toEntities(result, sessionId, versionId));
        return result;
    }

    private List<AlternativeDto> calculateAlternativesFromFactors(List<SwotFactorEntity> allFactors) {
        List<SwotFactorEntity> internalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("strong") || f.getType().equalsIgnoreCase("weak"))
                .toList();

        List<SwotFactorEntity> externalFactors = allFactors.stream()
                .filter(f -> f.getType().equalsIgnoreCase("opportunity") || f.getType().equalsIgnoreCase("threat"))
                .toList();

        List<AlternativeDto> alternatives = new ArrayList<>();

        for (SwotFactorEntity internal : internalFactors) {
            for (SwotFactorEntity external : externalFactors) {
                double ra = 0;
                double dPlusSum = 0;
                double dMinusSum = 0;

                for (double alpha : ALPHA_LEVELS) {
                    double internalUserCoefficient = userSessionRepository.findSwotUserSessionBySessionIdAndUserId(internal.getSessionId(), internal.getUserId())
                            .get()
                            .getUserCoefficient();
                    double externalUserCoefficient = userSessionRepository.findSwotUserSessionBySessionIdAndUserId(external.getSessionId(), external.getUserId())
                            .get()
                            .getUserCoefficient();

                    double x = alphaCutMassCenter(internal, alpha) * internalUserCoefficient;
                    double y = alphaCutMassCenter(external, alpha) * externalUserCoefficient;

                    double dPlus = Math.sqrt(Math.pow(1 - x, 2) + Math.pow(1 - y, 2));
                    double dMinus = Math.sqrt(x * x + y * y);
                    double closeness = dMinus / (dPlus + dMinus);

                    ra += alpha * closeness;
                    dPlusSum += dPlus;
                    dMinusSum += dMinus;
                }

                alternatives.add(new AlternativeDto(
                        internal.getTitle(),
                        external.getTitle(),
                        trapezoidalMassCenter(internal),
                        trapezoidalMassCenter(external),
                        dPlusSum / ALPHA_LEVELS.size(),
                        dMinusSum / ALPHA_LEVELS.size(),
                        ra,
                        defineStrategy(internal.getType(), external.getType())
                ));
            }
        }

        return alternatives.stream()
                .sorted(Comparator.comparingDouble(AlternativeDto::getCloseness).reversed())
                .toList();
    }

    private List<SwotAlternativeEntity> toEntities(List<AlternativeDto> dtos, Long sessionId, Long versionId) {
        if (sessionId == null || versionId == null) return List.of();
        return dtos.stream().map(dto -> SwotAlternativeEntity.builder()
                .sessionId(sessionId)
                .versionId(versionId)
                .internalFactor(dto.getInternalFactor())
                .externalFactor(dto.getExternalFactor())
                .internalMassCenter(dto.getInternalMassCenter())
                .externalMassCenter(dto.getExternalMassCenter())
                .dPlus(dto.getDPlus())
                .dMinus(dto.getDMinus())
                .closeness(dto.getCloseness())
                .strategyType(dto.getStrategyType())
                .build()).toList();
    }

    private double trapezoidalMassCenter(SwotFactorEntity f) {
        return (f.getWeightMin() + 2 * f.getWeightAvg1() + 2 * f.getWeightAvg2() + f.getWeightMax()) / 6;
    }

    private double alphaCutMassCenter(SwotFactorEntity f, double alpha) {
        double left = f.getWeightMin() + (f.getWeightAvg1() - f.getWeightMin()) * alpha;
        double right = f.getWeightMax() - (f.getWeightMax() - f.getWeightAvg2()) * alpha;
        return (left + right) / 2;
    }

    private String defineStrategy(String internalType, String externalType) {
        return switch ((internalType + externalType).toLowerCase()) {
            case "strongopportunity" -> "SO";
            case "strongthreat" -> "ST";
            case "weakthreat" -> "WT";
            case "weakopportunity" -> "WO";
            default -> "UNDEFINED";
        };
    }

    public List<SwotAlternativeEntity> getBySessionAndVersion(Long sessionId, Long versionId) {
        return alternativeRepository.findAllBySessionIdAndVersionId(sessionId, versionId);
    }
}
