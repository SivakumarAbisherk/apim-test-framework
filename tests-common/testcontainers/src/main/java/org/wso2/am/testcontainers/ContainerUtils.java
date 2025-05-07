package org.wso2.am.testcontainers;

import java.io.*;
import java.nio.file.*;

public class ContainerUtils {
    public static File copyToTempDir(String sourceDir) throws IOException {
        Path tempDir = Files.createTempDirectory("wso2apim");
        Path sourcePath = Paths.get(sourceDir);
        Files.walk(sourcePath)
                .forEach(src -> {
                    try {
                        Path dest = tempDir.resolve(sourcePath.relativize(src));
                        if (Files.isDirectory(src)) {
                            Files.createDirectories(dest);
                        } else {
                            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        return tempDir.toFile();
    }
}
