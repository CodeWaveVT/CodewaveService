package edu.vt.codewaveservice.controller;
import java.util.Date;

import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static edu.vt.codewaveservice.utils.ConvertUtil.readPdfContent;

@RestController
public class ConvertController {
    @Resource
    private TaskService taskService;
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                // Convert the EPUB file to TXT

                String txtContent = readPdfContent(file);
                System.out.println("finish !!!!!!"+txtContent);

//                Task task = new Task();
//                task.setId("0L");
//                task.setEbookname("testbook");
//                task.setBookType("epub");
//                task.setEbookTextData(txtContent);
//                task.setGenAudioUrl("");
//                task.setStatus("");
//                task.setExecMessage("");
//                task.setUserId(-1L);
//                task.setCreateTime(new Date());
//                task.setUpdateTime(new Date());
//                task.setIsDelete(0);
//                taskService.save(task);

                // Here you can save the TXT content to a file or return it directly
                return new ResponseEntity<>(txtContent, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid file.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
