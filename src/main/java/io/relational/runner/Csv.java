package io.relational.runner;

import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*;

final class Csv {
  static List<List<String>> read(Path path) throws IOException {
    try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      List<List<String>> rows = new ArrayList<>(); List<String> row = new ArrayList<>(); StringBuilder field = new StringBuilder(); boolean quoted=false; int ch;
      while ((ch=r.read())!=-1) { char c=(char)ch; if (quoted) { if (c=='"') { r.mark(1); int n=r.read(); if (n=='"') field.append('"'); else { quoted=false; if(n!=-1) r.reset(); } } else field.append(c); }
        else if (c=='"' && field.length()==0) quoted=true; else if (c==',') { row.add(field.toString()); field.setLength(0); }
        else if (c=='\n') { row.add(trimCr(field)); rows.add(row); row=new ArrayList<>(); field=new StringBuilder(); }
        else field.append(c);
      }
      if (field.length()>0 || !row.isEmpty()) { row.add(trimCr(field)); rows.add(row); }
      return rows;
    }
  }
  private static String trimCr(StringBuilder b){ int n=b.length(); if(n>0 && b.charAt(n-1)=='\r') return b.substring(0,n-1); return b.toString(); }
  static String escape(String s){ if(s==null) return ""; boolean q=s.indexOf(',')>=0||s.indexOf('"')>=0||s.indexOf('\n')>=0||s.indexOf('\r')>=0; String v=s.replace("\"","\"\""); return q?'"'+v+'"':v; }
}
