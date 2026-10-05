package io.relational.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class CsvTest {
  @TempDir Path tempDir;

  @Test
  void readsQuotedCommasQuotesAndNewlines() throws Exception {
    Path csv = tempDir.resolve("sample.csv");
    Files.writeString(csv, "id,name,note\n1,\"Lovelace, Ada\",\"said \"\"hello\"\"\"\n2,Grace,\"line one\nline two\"\n");

    List<List<String>> rows = Csv.read(csv);

    assertEquals(List.of("id", "name", "note"), rows.get(0));
    assertEquals(List.of("1", "Lovelace, Ada", "said \"hello\""), rows.get(1));
    assertEquals(List.of("2", "Grace", "line one\nline two"), rows.get(2));
  }

  @Test
  void escapesOnlyWhenNeeded() {
    assertEquals("Ada", Csv.escape("Ada"));
    assertEquals("\"Lovelace, Ada\"", Csv.escape("Lovelace, Ada"));
    assertEquals("\"said \"\"hello\"\"\"", Csv.escape("said \"hello\""));
    assertEquals("", Csv.escape(null));
  }
}
