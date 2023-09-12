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
    @TableId(type = IdType.AUTO)
    private Long id;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}