package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.LogTest;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersistenceXmlHelperTest {

    @Test
    void addDataSourceToPersistenceXmlTest() throws IOException {
        Log log = new LogTest();
        var jdbcName = "java:app/jdbc/SampleDataSource";
        File inputFile = new File(Objects.requireNonNull(PersistenceXmlHelperTest.class.getResource("/")).getFile());
        var xmlPath = inputFile.toPath();
        var helper = PersistenceXmlHelper.getInstance();
        helper.addDataSourceToPersistenceXml(
            xmlPath,
            log,
            "defaultPU",
            jdbcName
        );
        var lines = Files.readString(helper.getPersistencePath(xmlPath));
        assertTrue(lines.contains(jdbcName));
    }
}
