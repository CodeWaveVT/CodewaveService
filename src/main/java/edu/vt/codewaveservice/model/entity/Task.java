package edu.vt.codewaveservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * task info table
 * @TableName task
 */
@TableName(value ="task")
@Data
public class Task implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * upload ebook name
     */
    private String ebookname;

    /**
     * ebook type
     */
    private String bookType;

    /**
     * user upload ebook text
     */
    private String ebookTextData;

    /**
     * generated audio url
     */
    private String genAudioUrl;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * execute message
     */
    private String execMessage;

    /**
     * user id who start this task
     */
    private Long userId;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * logical delete
     */
    private Integer isDelete;

    /**
     * user upload origin ebook
     */
    private byte[] ebookOriginData;

    /**
     * generated audio file
     */
    private byte[] genAduioData;

    private String modelType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static class Builder {
        private Task task;

        public Builder() {
            task = new Task();
        }

        public Task build() {
            return task;
        }

        public Builder withId(String id) {
            task.setId(id);
            return this;
        }

        public Builder withStatus(String status) {
            task.setStatus(status);
            return this;
        }

        public Builder withGenAudioUrl(String url) {
            task.setGenAudioUrl(url);
            return this;
        }


        public Builder withEbookname(String ebookname) {
            task.setEbookname(ebookname);
            return this;
        }

        public Builder withBookType(String type) {
            task.setBookType(type);
            return this;
        }

        public Builder withUserId(long l) {
            task.setUserId(l);
            return this;
        }

        public Builder withEbookOriginData(byte[] ebookData) {
            task.setEbookOriginData(ebookData);
            return this;
        }

        public Builder withEbookTextData(String ebookTextData) {
            task.setEbookTextData(ebookTextData);
            return this;
        }

        public Builder withModelType(String modelType) {
            task.setModelType(modelType);
            return this;
        }
    }


}