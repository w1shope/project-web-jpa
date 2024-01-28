package com.example.demo.controller;

import com.example.demo.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileStore fileStore;

    @ResponseBody
    @GetMapping("/images/{storedFileName}")
    public Resource downloadImage(@PathVariable("storedFileName") String storedFileName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getStoredFullPath(storedFileName));
    }

}
