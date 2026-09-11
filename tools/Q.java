import java.sql.*;
public class Q {
  public static void main(String[] a) throws Exception {
    Class.forName("org.hsqldb.jdbcDriver");
    String sql = String.join(" ", a);
    try (Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/parabank","sa","");
         Statement st = c.createStatement()) {
      boolean isRs = st.execute(sql);
      if (!isRs) { System.out.println("rows affected: " + st.getUpdateCount()); return; }
      try (ResultSet rs = st.getResultSet()) {
        ResultSetMetaData m = rs.getMetaData();
        int n = m.getColumnCount();
        StringBuilder h = new StringBuilder();
        for (int i=1;i<=n;i++) h.append(String.format("%-22s", m.getColumnLabel(i)));
        System.out.println(h);
        System.out.println("-".repeat(Math.min(h.length(),140)));
        int r=0;
        while (rs.next() && r<60) {
          StringBuilder b = new StringBuilder();
          for (int i=1;i<=n;i++) b.append(String.format("%-22s", String.valueOf(rs.getString(i))));
          System.out.println(b); r++;
        }
        System.out.println("\n(" + r + " rows shown)");
      }
    }
  }
}
