package edu.vt.codewaveservice.processor;


import edu.vt.codewaveservice.processor.impl.EpubConverterProcessor;
import edu.vt.codewaveservice.processor.impl.PdfConverterProcessor;
import edu.vt.codewaveservice.processor.impl.TxtConverterProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ConverterProcessorFactory {

    public Processor getProcessor(String fileType) {
        switch (fileType.toLowerCase()) {
            case "epub":
                return new EpubConverterProcessor();
            case "txt":
                return new TxtConverterProcessor();
            case "pdf":
                return new PdfConverterProcessor();
            default:
                log.error("Unsupported file type: {}", fileType);
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public String getFileExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
