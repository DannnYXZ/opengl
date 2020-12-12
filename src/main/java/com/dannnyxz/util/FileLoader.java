package com.dannnyxz.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class FileLoader {

  public String readResourceFileToString(String resourceFilePath) {
    ClassLoader classLoader = getClass().getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream(resourceFilePath)) {
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load file from resources folder: " + resourceFilePath);
    }
  }
}
