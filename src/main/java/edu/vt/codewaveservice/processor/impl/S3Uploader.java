package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.utils.S3Utils;

import java.io.File;

public class S3Uploader implements Processor {
    @Override
    public void process(ProcessingContext context) {
        String outputFilePath = context.getFinalMp3Path();
        String fileName = new File(outputFilePath).getName();
        S3Utils s3Utils = new S3Utils();
        String s3Url = s3Utils.uploadFile(outputFilePath, fileName);

        context.getTempFileManager().deleteAllTempFiles();

        context.setFinalMp3Path(s3Url);
    }
}

