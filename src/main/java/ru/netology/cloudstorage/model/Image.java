package ru.netology.cloudstorage.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
public class Image extends BaseEntity {

//    @Column(name = "user_id")
//    private User user;
    
    @Column(name = "user_id")
    private long userId;
    
    @Column(name = "filename")
    private String fileName;

//    @Type(type = "org.hibernate.type.BlobType")
    @Lob
    @Column(name = "content", length = 20971520)
    private byte[] fileContent;

    @Column(name = "size")
    private long fileSize;
}
