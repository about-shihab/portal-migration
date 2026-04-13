package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.exceptions.FileStorageException;
import com.iict.buet.customer_portal.model.FileInfo;
import com.iict.buet.customer_portal.repository.FileInfoRepository;
import com.iict.buet.customer_portal.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final Logger logger = LogManager.getLogger(FileService.class);
    private final FileInfoRepository fileInfoRepo;
    private final EnvironmentProperties env;
    private final AuthUtils authUtils;

    @PostConstruct
    public void init() {
        Path fileStorageLocation = Paths.get(env.getFILE_UPLOAD_DIR()).toAbsolutePath().normalize();
        try {
            if (!Files.exists(fileStorageLocation))
                Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            logger.error(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
            throw new FileStorageException("Could not create the base directory where the uploaded files will be stored. Exception: "
                    + ex.getMessage());
        }
    }

    public FileInfo saveFileInfo(MultipartFile file, String filePath, String title) {
        FileInfo fileInfo = createFileInfo(file);
        fileInfo.setPath(filePath);
        fileInfo.setTitle(title);
        return fileInfoRepo.save(fileInfo);
    }

    private FileInfo createFileInfo(MultipartFile file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(file.getOriginalFilename());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setSize(file.getSize());
        fileInfo.setExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()));
        fileInfo.setCustomerCode(authUtils.getLoggedInUser());
        return fileInfo;
    }

    public String storeFile(MultipartFile file, Path basePath, boolean useDateWiseFolder) throws IOException {
        if (file.isEmpty()) throw new IllegalStateException("File is empty");

        Path targetPath = useDateWiseFolder ? basePath.resolve(java.time.LocalDate.now().toString()) : basePath;
        if (!Files.exists(targetPath)) Files.createDirectories(targetPath);

        Path destinationPath = targetPath.resolve(new Date().getTime() + "-" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return destinationPath.toString();
        } catch (IOException e) {
            logger.error("Failed to store file: " + NestedExceptionUtils.getMostSpecificCause(e).getMessage(), e);
            throw new FileStorageException("File upload failed: " + e.getMessage(), e);
        }
    }

    public Resource getFileResource(Long fileId, boolean checkPermission) throws IOException {
        FileInfo fileInfo = fileInfoRepo.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found with id " + fileId));

        if (checkPermission && !fileInfo.getCustomerCode().equals(authUtils.getLoggedInUser())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to download this file");
        }

        Path filePath = Paths.get(fileInfo.getPath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable");
        }
    }

    public ResponseEntity<Resource> buildFileResponse(Long fileId, boolean checkPermission) throws IOException {
        Resource resource = getFileResource(fileId, checkPermission);
        FileInfo fileInfo = getFileInfo(fileId);
        return buildFileResponse(resource, fileInfo);
    }

    public FileInfo getFileInfo(Long fileId) {
        return fileInfoRepo.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found with id " + fileId));
    }

    private ResponseEntity<Resource> buildFileResponse(Resource resource, FileInfo fileInfo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, fileInfo.getContentType())
                .body(resource);
    }

}
