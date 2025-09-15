package com.sahabatquran.webapp.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

@Slf4j
@Service
public class EmailTemplateService {

    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    /**
     * Generates HTML email content using Mustache template.
     *
     * @param emailData Student report data
     * @return HTML email content
     */
    public String generateEmailContent(StudentReportEmailDto emailData) {
        try {
            return renderTemplate("templates/email/student-report-html.mustache", emailData);
        } catch (Exception e) {
            log.error("Failed to generate HTML email content, falling back to plain text", e);
            return generatePlainTextEmailContent(emailData);
        }
    }

    /**
     * Generates plain text email content using Mustache template.
     * Used as fallback when HTML template fails.
     *
     * @param emailData Student report data
     * @return Plain text email content
     */
    public String generatePlainTextEmailContent(StudentReportEmailDto emailData) {
        try {
            return renderTemplate("templates/email/student-report.mustache", emailData);
        } catch (Exception e) {
            log.error("Failed to generate plain text email content", e);
            throw new RuntimeException("Failed to generate email content", e);
        }
    }

    /**
     * Generates email subject line.
     *
     * @param emailData Student report data
     * @return Email subject
     */
    public String generateEmailSubject(StudentReportEmailDto emailData) {
        return String.format("Laporan Akademik - %s - %s",
            emailData.getStudentName(),
            emailData.getTermName());
    }

    /**
     * Renders a Mustache template with the provided data.
     *
     * @param templatePath Path to the template file
     * @param data Data to render
     * @return Rendered template content
     * @throws IOException If template cannot be loaded
     */
    private String renderTemplate(String templatePath, Object data) throws IOException {
        log.debug("Rendering template: {}", templatePath);

        // Load template from classpath
        ClassPathResource templateResource = new ClassPathResource(templatePath);
        if (!templateResource.exists()) {
            throw new IOException("Template not found: " + templatePath);
        }

        try (InputStreamReader reader = new InputStreamReader(templateResource.getInputStream())) {
            Mustache template = mustacheFactory.compile(reader, templatePath);

            StringWriter writer = new StringWriter();
            template.execute(writer, data);

            String result = writer.toString();
            log.debug("Template rendered successfully, content length: {} characters", result.length());

            return result;
        }
    }

}