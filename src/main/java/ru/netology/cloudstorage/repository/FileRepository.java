package ru.netology.cloudstorage.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.model.Image;
import ru.netology.cloudstorage.model.Status;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


public interface FileRepository extends JpaRepository<Image, Long> {

    Image findByFileNameAndUserIdAndStatus(String fileName, long userId, Status status);

    @Query("SELECT i.id FROM Image i WHERE i.userId = :userId AND i.fileName = :filename AND i.status = :status")
    Long findRecordId(@Param("userId") long userId, @Param("filename") String fileName, @Param("status") Status status);

    @Transactional
    @Modifying
    @Query("UPDATE Image i SET i.status = :status, i.updated = :update WHERE i.id = :recordId")
    void deleteById(@Param("recordId") long recordId, @Param("status") Status status, @Param("update") Timestamp time);

    Image findById(long recordId);

    @Transactional
    @Modifying
    @Query("UPDATE Image i SET i.fileName = :name, i.updated = :update WHERE i.id = :recordId")
    void renameFile(@Param("recordId") long recordId, @Param("name") String fileName, @Param("update") Timestamp time);

    @Query("SELECT i FROM Image i WHERE i.userId = :userId AND i.status = :status ORDER BY i.id DESC")
    List<Image> getFileList(@Param("userId") long userId, @Param("status") Status status, Pageable pageable);

}
