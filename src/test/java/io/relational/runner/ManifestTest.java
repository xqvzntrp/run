package io.relational.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class ManifestTest {
  @TempDir Path tempDir;

  @Test
  void readsSortsAndParsesManifestEntries() throws Exception {
    Path manifestDir = tempDir.resolve("package/manifest");
    Files.createDirectories(manifestDir);
    Path manifestPath = manifestDir.resolve("manifest.csv");
    Files.writeString(manifestPath,
        "seq,enabled,artifact_path,artifact_kind,output_path,source_path,target_table,authority_zone,purpose\n"
      + "20,Y,verify/check.sql,VERIFY,,,,,check\n"
      + "10,true,sql/create.sql,SQL,,,,,create\n");

    Manifest manifest = Manifest.read(manifestPath);

    assertEquals(2, manifest.entries().size());
    assertEquals(10, manifest.entries().get(0).seq());
    assertEquals(ArtifactKind.SQL, manifest.entries().get(0).kind());
    assertEquals(20, manifest.entries().get(1).seq());
    assertTrue(manifest.packageRoot().endsWith("package"));
  }

  @Test
  void rejectsDuplicateSequenceNumbers() throws Exception {
    Path manifestDir = tempDir.resolve("package/manifest");
    Files.createDirectories(manifestDir);
    Path manifestPath = manifestDir.resolve("manifest.csv");
    Files.writeString(manifestPath,
        "seq,enabled,artifact_path,artifact_kind,output_path,source_path,target_table\n"
      + "10,Y,a.sql,SQL,,,,\n"
      + "10,Y,b.sql,SQL,,,,\n");

    IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> Manifest.read(manifestPath));
    assertTrue(error.getMessage().contains("Duplicate seq"));
  }

  @Test
  void rejectsPathsThatEscapePackageRoot() throws Exception {
    Path manifestDir = tempDir.resolve("package/manifest");
    Files.createDirectories(manifestDir);
    Path manifestPath = manifestDir.resolve("manifest.csv");
    Files.writeString(manifestPath,
        "seq,enabled,artifact_path,artifact_kind,output_path,source_path,target_table\n"
      + "10,Y,sql/a.sql,SQL,,,,\n");
    Manifest manifest = Manifest.read(manifestPath);

    assertThrows(IllegalArgumentException.class, () -> manifest.resolve("../outside.sql"));
  }
}
