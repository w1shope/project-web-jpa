package com.example.demo.repository;


import com.example.demo.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update File f set f.uploadFilename = :uploadFilename, f.storedFilePath = :storedFilePath where f.id = :fileId")
    void changeFile(@Param(value = "fileId") Long id, @Param(value = "uploadFilename") String uploadFilename, @Param(value = "storedFilePath") String storedFilePath);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from File f where f.id = :fileId")
    void removeFile(@Param(value = "fileId") Long id);
}
