package io.relational.runner;

import java.io.*; import java.nio.file.*; import java.util.*;

final class SqlScripts {
  static List<String> statements(Path path) throws IOException {
    List<String> out=new ArrayList<>(); StringBuilder b=new StringBuilder(); boolean block=false;
    for(String raw:Files.readAllLines(path)){ String t=raw.trim(); if(t.isEmpty()||t.startsWith("--")) { b.append(raw).append('\n'); continue; }
      if(b.length()==0){ String u=t.toUpperCase(Locale.ROOT); block=u.startsWith("CREATE OR REPLACE PACKAGE")||u.startsWith("CREATE OR REPLACE PROCEDURE")||u.startsWith("CREATE OR REPLACE FUNCTION")||u.startsWith("CREATE OR REPLACE TRIGGER")||u.startsWith("DECLARE")||u.startsWith("BEGIN"); }
      if(block && t.equals("/")){ add(out,b); block=false; continue; }
      b.append(raw).append('\n');
      if(!block && t.endsWith(";")){ int p=b.lastIndexOf(";"); b.deleteCharAt(p); add(out,b); }
    }
    add(out,b); return out;
  }
  private static void add(List<String> o,StringBuilder b){String s=b.toString().trim();b.setLength(0);if(!s.isEmpty())o.add(s);}
}
