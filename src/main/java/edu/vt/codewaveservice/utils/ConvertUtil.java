package edu.vt.codewaveservice.utils;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

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
}
