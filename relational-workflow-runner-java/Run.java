import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Run {
  public static void main(String[] args) throws Exception {
    Path jar = Path.of("target", "relational-workflow-runner.jar");
    if (needsBuild(jar)) {
      String mvn = Files.isExecutable(Path.of("mvnw")) ? "./mvnw" : "mvn";
      int build = new ProcessBuilder(mvn, "-q", "-DskipTests", "package").inheritIO().start().waitFor();
      if (build != 0) System.exit(build);
    }
    List<String> command = new ArrayList<>();
    command.add("java"); command.add("-jar"); command.add(jar.toString());
    command.addAll(List.of(args));
    System.exit(new ProcessBuilder(command).inheritIO().start().waitFor());
  }

  private static boolean needsBuild(Path jar) throws IOException {
    if (!Files.isRegularFile(jar)) return true;
    long built = Files.getLastModifiedTime(jar).toMillis();
    if (Files.getLastModifiedTime(Path.of("pom.xml")).toMillis() > built) return true;
    Path src = Path.of("src", "main", "java");
    if (!Files.isDirectory(src)) return false;
    try (Stream<Path> files = Files.walk(src)) {
      return files.filter(p -> p.toString().endsWith(".java")).anyMatch(p -> modifiedAfter(p, built));
    }
  }

  private static boolean modifiedAfter(Path path, long instant) {
    try { return Files.getLastModifiedTime(path).toMillis() > instant; }
    catch (IOException e) { throw new RuntimeException(e); }
  }
}
