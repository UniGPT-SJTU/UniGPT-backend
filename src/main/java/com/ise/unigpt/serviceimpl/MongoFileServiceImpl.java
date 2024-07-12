package com.ise.unigpt.serviceimpl;
import com.ise.unigpt.service.MongoFileService;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static com.mongodb.client.model.Filters.eq;

@Service
public class MongoFileServiceImpl implements MongoFileService{

    @Autowired
    private MongoDatabaseFactory mongoDatabaseFactory;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("contentType", file.getContentType()));

            ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream(), options);
            return fileId.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String id) {
        try {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(new ObjectId(id), outputStream);

            GridFSFile gridFSFile = gridFSBucket.find(eq("_id", new ObjectId(id))).first();
            if (gridFSFile == null) {
                throw new RuntimeException("File not found");
            }

            String fileName = gridFSFile.getFilename();
            String contentType = gridFSFile.getMetadata().getString("contentType");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}
