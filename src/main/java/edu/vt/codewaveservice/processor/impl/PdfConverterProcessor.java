package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class PdfConverterProcessor implements Processor {

    public void process(ProcessingContext context) {
        MultipartFile epubFile = context.getFile();
        try {
            String txtContent = readPdfContent(epubFile);
            context.setText(txtContent);
        } catch (Exception e) {
            log.error("Error during EPUB conversion", e);
            throw new RuntimeException(e);
        }
    }

    public static String readPdfContent(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            PDDocument document = PDDocument.load(in);
            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(document);
            document.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
