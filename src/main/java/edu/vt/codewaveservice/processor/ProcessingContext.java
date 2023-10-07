package edu.vt.codewaveservice.processor;

import edu.vt.codewaveservice.utils.TempFileManager;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
@Data
public class ProcessingContext {

    private byte[] file;
    private String text;
    private String bookName;
    private String fileName;
    private List<String> subTexts;
    private List<File> mp3Files;
    private String finalMp3Path;
    private TempFileManager tempFileManager;
    private String fileType;
}
