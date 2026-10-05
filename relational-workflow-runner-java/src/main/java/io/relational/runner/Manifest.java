package io.relational.runner;

import java.io.*; import java.nio.file.*; import java.util.*;

enum ArtifactKind { SQL, LOAD_CSV, VERIFY, EXPORT }
record ManifestEntry(int seq, boolean enabled, String artifactPath, ArtifactKind kind, String outputPath, String sourcePath, String targetTable, String authorityZone, String purpose) {}
record Manifest(Path manifestPath, Path packageRoot, List<ManifestEntry> entries) {
  static Manifest read(Path path) throws IOException {
    if(!Files.isRegularFile(path)) throw new IllegalArgumentException("Manifest not found: " + path);
    List<List<String>> rows=Csv.read(path); if(rows.isEmpty()) throw new IllegalArgumentException("Manifest is empty");
    Map<String,Integer> h=headers(rows.get(0)); require(h,"seq","enabled","artifact_path","artifact_kind","output_path","source_path","target_table");
    List<ManifestEntry> out=new ArrayList<>(); Set<Integer> seen=new HashSet<>();
    for(int i=1;i<rows.size();i++){ List<String> r=rows.get(i); if(r.stream().allMatch(String::isBlank)) continue; int seq=Integer.parseInt(get(r,h,"seq").trim()); if(!seen.add(seq)) throw new IllegalArgumentException("Duplicate seq: "+seq);
      boolean enabled=parseBool(get(r,h,"enabled")); ArtifactKind kind=ArtifactKind.valueOf(get(r,h,"artifact_kind").trim().toUpperCase(Locale.ROOT));
      out.add(new ManifestEntry(seq,enabled,get(r,h,"artifact_path"),kind,get(r,h,"output_path"),get(r,h,"source_path"),get(r,h,"target_table"),get(r,h,"authority_zone"),get(r,h,"purpose")));
    }
    out.sort(Comparator.comparingInt(ManifestEntry::seq));
    Path parent=path.toAbsolutePath().normalize().getParent(); if(parent==null||parent.getParent()==null) throw new IllegalArgumentException("Manifest must be under <package>/manifest/");
    return new Manifest(path.toAbsolutePath().normalize(), parent.getParent(), List.copyOf(out));
  }
  Path resolve(String rel){ if(rel==null||rel.isBlank()) return null; Path p=packageRoot.resolve(rel).normalize(); if(!p.startsWith(packageRoot)) throw new IllegalArgumentException("Path escapes package root: "+rel); return p; }
  private static Map<String,Integer> headers(List<String> row){Map<String,Integer> m=new HashMap<>(); for(int i=0;i<row.size();i++)m.put(row.get(i).trim(),i);return m;}
  private static void require(Map<String,Integer> h,String... n){for(String s:n)if(!h.containsKey(s))throw new IllegalArgumentException("Manifest missing column: "+s);}
  private static String get(List<String> r,Map<String,Integer> h,String n){Integer i=h.get(n); return i==null||i>=r.size()?"":r.get(i);}
  private static boolean parseBool(String v){return switch(v.trim().toLowerCase(Locale.ROOT)){case "y","yes","true","1"->true;case "n","no","false","0"->false;default->throw new IllegalArgumentException("Invalid enabled value: "+v);};}
}
