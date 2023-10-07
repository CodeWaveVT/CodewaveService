package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EpubConverterProcessor implements Processor {

    public void process(ProcessingContext context) {
        byte[] epubData = context.getFile();
        if (epubData == null || epubData.length == 0) {
            log.debug("No EPUB data available for processing");
            return;
        }

        //MultipartFile epubFile = context.getFile();
        try {
//            String txtContent = convertEpubToTxt(epubFile);
//            context.setText(txtContent);
//            System.out.println(txtContent);
            String txtContent = convertEpubToTxt(epubData);
            context.setText(txtContent);
            System.out.println(txtContent);

        } catch (Exception e) {
            log.debug("Error during EPUB conversion", e);
            //throw new RuntimeException(e);
        }
    }

    //    private String convertEpubToTxt(MultipartFile file) throws Exception {
//        Book book = (new EpubReader()).readEpub(file.getInputStream());
//        StringBuilder txtContent = new StringBuilder();
//
//        for (SpineReference spineRef : book.getSpine().getSpineReferences()) {
//            Resource resource = spineRef.getResource();
//            String content = new String(resource.getData(), StandardCharsets.UTF_8);
//            String plainText = Jsoup.parse(content).text();
//            txtContent.append(plainText).append("\n\n");
//        }
//
//        return txtContent.toString();
//    }
    private String convertEpubToTxt(byte[] data) throws Exception {
        try (InputStream in = new ByteArrayInputStream(data)) {
            Book book = (new EpubReader()).readEpub(in);
            StringBuilder txtContent = new StringBuilder();

            for (SpineReference spineRef : book.getSpine().getSpineReferences()) {
                Resource resource = spineRef.getResource();
                String content = new String(resource.getData(), StandardCharsets.UTF_8);
                String plainText = Jsoup.parse(content).text();
                txtContent.append(plainText).append("\n\n");
            }

            return txtContent.toString();
        }
    }


}

