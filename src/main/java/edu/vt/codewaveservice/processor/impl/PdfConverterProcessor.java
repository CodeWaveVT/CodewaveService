package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.processor.ProcessorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class PdfConverterProcessor implements Processor {

    public void process(ProcessingContext context) {
        System.out.println("input: "+context);
        //MultipartFile epubFile = context.getFile();
        try {
//            String txtContent = readPdfContent(epubFile);
//            context.setText(txtContent);

            byte[] ebookData = context.getFile();
            if (ebookData == null || ebookData.length == 0) {
                throw new ProcessorException("No data available for processing", this);
            }

            // 使用byte[]数据进行PDF转换。例如：
            String extractedText = readPdfContentByte(ebookData);
            context.setText(extractedText);


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

    public static String readPdfContentByte(byte[] data) {
        try (InputStream in = new ByteArrayInputStream(data)) {
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
