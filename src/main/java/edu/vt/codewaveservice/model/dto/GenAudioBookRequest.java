package edu.vt.codewaveservice.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenAudioBookRequest implements Serializable {
    private String bookName;

    private String bookType;

    private String bookAuthor;

    private static final long serialVersionUID = 1L;
}
