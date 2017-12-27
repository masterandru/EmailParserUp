package com.storage;

import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    //void store(MultipartFile file);
    void store(ByteArrayInputStream inputStream, String filename);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
