package edu.vt.codewaveservice.utils;

import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
@Slf4j
public class ConvertUtil {

    public static String convertEpubToTxt(MultipartFile file) throws Exception {
        Book book = (new EpubReader()).readEpub(file.getInputStream());
        StringBuilder txtContent = new StringBuilder();

        // Loop through all spine references and get the associated resources
        for (SpineReference spineRef : book.getSpine().getSpineReferences()) {
            Resource resource = spineRef.getResource();
            String content = new String(resource.getData(), StandardCharsets.UTF_8);

            String plainText = Jsoup.parse(content).text();

            txtContent.append(plainText).append("\n\n");
        }
        return txtContent.toString();
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

    public String readTxtContent(MultipartFile file) throws IOException {

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

}
