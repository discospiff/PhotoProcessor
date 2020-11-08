package com.myplantdiary.postprocessor.photoprocessor;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PhotoProcessor {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics="photoIn", groupId="myplantdiary")
    public void processPhoto(String path) {
        System.out.println("Path is: " + path);
        File file = new File(path);
        Path photoPath = Paths.get(path).getParent();
        Path fileName = Paths.get(path).getFileName();
        String thumbnailPath = photoPath + File.separator + "thumbnail" + File.separator + fileName;
        File thumbnailFile = new File(thumbnailPath);

        String watermarkPath = photoPath + File.separator + "watermark.png";
        File watermarkFile = new File(watermarkPath);

        try {

            BufferedImage watermark = ImageIO.read(watermarkFile);
            Thumbnails.of(file).scale(1).watermark(Positions.BOTTOM_RIGHT, watermark, 0.9F).toFile(file);
            Thumbnails.of(file).size(100, 100).toFile(thumbnailFile);

            kafkaTemplate.send("photoOut", path);
        } catch (IOException e) {
            e.printStackTrace();
            kafkaTemplate.send("photoException", path);
        }

    }


}
