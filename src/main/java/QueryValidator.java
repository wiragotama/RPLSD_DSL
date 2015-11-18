import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wiragotama on 11/18/15.
 * This class use to verify whether query is correct or wrong
 */
public class QueryValidator {

    private Query query;
    private String user;
    private String password;
    private String url;
    private String dbName;

    /**
     * Default constuctor
     * @param q
     */
    public QueryValidator(Query q) {
        this.query = q;
        try {
            BufferedReader file = new BufferedReader(new FileReader("db.option"));
            dbName = file.readLine();
            user = file.readLine();
            password = file.readLine();
            url = "jdbc:mysql://localhost:3306/"+dbName;
        } catch (Exception e) {
            System.out.println("Config file not found or paramters is uncomplete or no connection to database");
        }
    }

    /**
     * Verify whether query is correct to the databse
     * @return
     */
    public boolean verify() {
        boolean flag = false; //salah kecuali ada yang bilang dia benar
        try {
            Connection con = (Connection) DriverManager.getConnection(url, user, password);
            Statement st = null;
            st = (Statement) con.createStatement();
            ResultSet res = st.executeQuery("show tables");

            boolean found = false;
            while (res.next() && !found) {
                if (query.getTableName().equalsIgnoreCase(res.getString(1))) {
                    found = true;
                }
            }

            if (found) {
                List<String> colsName = new ArrayList<String>();
                List<String> colsType = new ArrayList<String>();

                //sekarang verify tiap kolom di table;
                res = st.executeQuery("SHOW COLUMNS FROM " + query.getTableName() + " FROM " + this.dbName + ";");
                while (res.next()) {
                    colsName.add(res.getString(1));
                    colsType.add(res.getString(2));
                }

                List<String> cols = query.getColumns();
                flag = true;
                for (int i=0; i<cols.size() && flag; i++) {
                    if (!colsName.contains(cols.get(i))) {
                        flag = false;
                        System.out.println("Error [columns]: column(s) name not known");
                    }
                }

                List<Pair<String, String>> instance= query.getInstance();
                for (int i=0; i<instance.size() && flag; i++) {
                    if (!colsName.contains(instance.get(i).getKey())) {
                        flag = false;
                        System.out.println("Error [instance]: column(s) name not known");
                    }
                    else { //if column exist
                        flag = verifyValueType(colsType.get(colsName.indexOf(instance.get(i).getKey())), instance.get(i).getValue());
                        if (!flag)
                            System.out.println("Error [instance]: incorrect value type/length");
                    }
                }

                List<Pair<String, String>> filter= query.getFilter();
                for (int i=0; i<filter.size() && flag; i++) {
                    if (!colsName.contains(filter.get(i).getKey())) {
                        flag = false;
                        System.out.println("Error [filter]: column(s) name not known");
                    }
                    else {
                        flag = verifyValueType(colsType.get(colsName.indexOf(filter.get(i).getKey())), filter.get(i).getValue());
                        if (!flag)
                            System.out.println("Error [filter]: incorrect value type/length");
                    }
                }

                return flag;
            }
            else flag = false;

        } catch (SQLException e) {
            System.out.println("SQL connection error");
        }
        return flag;
    }

    /**
     * Check if value is correct to the column type
     * @param colType
     * @param value
     * @return boolean
     */
    private boolean verifyValueType(String colType, String value) {
        if (value.matches("^[0-9]+$") && colType.contains("int")) {
            int maxLength = Integer.valueOf(colType.substring(colType.length()-3, colType.length()-1));
            if (value.length() <= maxLength) {
                return true;
            }
            else return false;
        }
        else if (colType.contains("varchar") || colType.contains("text")) {
            int maxLength = Integer.valueOf(colType.substring(colType.length()-3, colType.length()-1));
            if (value.length() <= maxLength) {
                return true;
            }
            else return false;
        }
        else return false;
    }
}
