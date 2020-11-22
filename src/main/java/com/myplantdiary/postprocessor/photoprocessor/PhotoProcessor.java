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

    @KafkaListener(topics="photoin", groupId="photoprocessor")
    public void processPhoto(String path) {
        System.out.println("Path is: " + path);
        if (path.contains("Photo")) {
            kafkaTemplate.send("photoOut", path);
        } else {
            kafkaTemplate.send("photoException", path);
        }

    }


}
