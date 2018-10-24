package business.dao.data;



import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database {
    //@Resource(lookup = "jdbc/awd_db")

    private static DataSource dataSource;
    private static InitialContext ctx;



    private static void init() {
        try {
            ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/swa");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDatasource() {
        if (dataSource == null)
            init();
        return dataSource;
    }
}
