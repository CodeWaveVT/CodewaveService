package edu.vt.codewaveservice.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TaskVo {
    private String taskId;

    private String bookName;

    private String author;

    private String bookUrl;

    private Date createTime;

    private String status;
}
