package io.relational.runner;

import java.nio.file.*;
import java.util.*;

record Cli(boolean dryRun, Path manifest, Integer from, Integer through) {
  static Cli parse(String[] args) {
    boolean dry = false, run = false; Path manifest = null; Integer from = null, through = null;
    for (int i=0;i<args.length;i++) switch (args[i]) {
      case "--dry-run" -> dry = true;
      case "--run" -> run = true;
      case "--manifest" -> manifest = Path.of(requireValue(args, ++i, "--manifest"));
      case "--from" -> from = Integer.valueOf(requireValue(args, ++i, "--from"));
      case "--through" -> through = Integer.valueOf(requireValue(args, ++i, "--through"));
      case "--help", "-h" -> { usage(); System.exit(0); }
      default -> throw new IllegalArgumentException("Unknown argument: " + args[i]);
    }
    if (dry == run) throw new IllegalArgumentException("Specify exactly one of --dry-run or --run");
    if (manifest == null) throw new IllegalArgumentException("--manifest is required");
    if (from != null && through != null && from > through) throw new IllegalArgumentException("--from must be <= --through");
    return new Cli(dry, manifest.toAbsolutePath().normalize(), from, through);
  }
  private static String requireValue(String[] a, int i, String opt) { if (i >= a.length) throw new IllegalArgumentException(opt + " requires a value"); return a[i]; }
  private static void usage() { System.out.println("Usage: java Run.java (--dry-run | --run) --manifest <manifest.csv> [--from SEQ] [--through SEQ]"); }
}
