package staffme.service.impl;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import staffme.service.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("temp-file", multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);


        return this.cloudinary
                .uploader()
                .upload(file, new HashMap())
                .get("url")
                .toString();
    }

    @Override
    public void deleteImage(String url) throws IOException {
        this.cloudinary.uploader()
                .destroy(url, new HashMap());
    }
}
