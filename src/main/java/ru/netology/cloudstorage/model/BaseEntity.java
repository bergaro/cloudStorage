package ru.netology.cloudstorage.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;


@MappedSuperclass
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created", insertable = false)
    private Date created;

    @LastModifiedDate
    @Column(name = "updated", insertable = false)
    private Date updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", insertable = false)
    private Status status;
}
