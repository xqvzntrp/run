package io.relational.runner;

import java.sql.*;

record DbConfig(String url,String user,String password) {
  static DbConfig env(){ String dsn=System.getenv("ORACLE_DSN"); String host=env("ORACLE_HOST","localhost"),port=env("ORACLE_PORT","1521"),service=env("ORACLE_SERVICE","FREEPDB1"); String url=(dsn!=null&&!dsn.isBlank())?"jdbc:oracle:thin:@//"+dsn:"jdbc:oracle:thin:@//"+host+":"+port+"/"+service; return new DbConfig(url, required("ORACLE_USER"), required("ORACLE_PASSWORD")); }
  private static String env(String n,String d){String v=System.getenv(n);return v==null||v.isBlank()?d:v;} private static String required(String n){String v=System.getenv(n);if(v==null||v.isBlank())throw new IllegalArgumentException(n+" is required");return v;}
}
final class Db { static Connection connect() throws SQLException { DbConfig c=DbConfig.env(); return DriverManager.getConnection(c.url(),c.user(),c.password()); } }
