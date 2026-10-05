package io.relational.runner;

import java.nio.file.*;

public final class Main {
  public static void main(String[] args) {
    try {
      Cli cli = Cli.parse(args);
      Manifest manifest = Manifest.read(cli.manifest());
      new WorkflowRunner(cli, manifest).execute();
    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      if (System.getenv("RUNNER_DEBUG") != null) e.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
