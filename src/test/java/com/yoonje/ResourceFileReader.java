package com.yoonje;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceFileReader {

    @SneakyThrows
    public static String readFile(String filePath) {
        Resource resource = new ClassPathResource(filePath);
        Reader in = new InputStreamReader(resource.getInputStream());
        return FileCopyUtils.copyToString(in);
    }

}
