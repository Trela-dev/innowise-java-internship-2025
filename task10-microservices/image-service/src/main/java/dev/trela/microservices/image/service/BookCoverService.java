package dev.trela.microservices.image.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import dev.trela.microservices.image.dto.ImageData;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BookCoverService {


    private final GridFSBucket gridFSBucket;

    public String uploadCoverImage(MultipartFile file) throws IOException {
        ObjectId fileId;

        Document metadata = new Document();
        metadata.put("contentType", file.getContentType());

        GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);

        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(file.getOriginalFilename(), options)) {
            uploadStream.write(file.getBytes());
            fileId = uploadStream.getObjectId();
        }
        return fileId.toHexString();
    }

    public ImageData getCoverImage(String fileId) throws IOException{
        ObjectId objectId = new ObjectId(fileId);
        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id",objectId)).first();

        if(gridFSFile == null){
            throw new FileNotFoundException("File not found with id: " + fileId);
        }
        String contentType = "application/octet-stream";
        if(gridFSFile.getMetadata() != null && gridFSFile.getMetadata().getString("contentType") != null){
            contentType = gridFSFile.getMetadata().getString("contentType");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        gridFSBucket.downloadToStream(objectId, outputStream);
        return new ImageData(outputStream.toByteArray(),contentType);
    }


}
