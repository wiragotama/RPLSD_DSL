import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wiragotama on 11/18/15.
 */
public class Query {
    private String command; //insert, update, read, delete
    private String tableName;
    private List<Pair<String, String>> instance; //all (key,value) pairs in instance
    private List<String> columns; //list of columns name, for select
    private List<Pair<String, String>> filter; //all (key,value) pairs for filter

    /**
     * Default constructor
     */
    public Query() {
        this.command = "";
        this.tableName = "";
        this.instance = new ArrayList<Pair<String, String>>();
        this.columns = new ArrayList<String>();
        this.filter = new ArrayList<Pair<String, String>>();
    }

    /**
     * Constructor, specific for insert command
     * @param command
     * @param tableName
     * @param instance
     */
    public Query(String command, String tableName, List<Pair<String, String>> instance) {
        this.command = command;
        this.tableName = tableName;
        this.instance = new ArrayList<Pair<String, String>>();
        for (int i=0; i<instance.size(); i++) {
            Pair<String, String> pair = new Pair(instance.get(i).getKey(), instance.get(i).getValue());
            this.instance.add(pair);
        }
        this.columns = new ArrayList<String>();
        this.filter = new ArrayList<Pair<String, String>>();
    }

    /**
     * Constructor, specific for update command
     * @param command
     * @param tableName
     * @param filter
     * @param instance
     */
    public Query(String command, String tableName, List<Pair<String, String>> filter, List<Pair<String, String>> instance) {
        this.command = command;
        this.tableName = tableName;
        this.filter = new ArrayList<Pair<String, String>>();
        for (int i=0; i<filter.size(); i++) {
            Pair<String, String> pair = new Pair(filter.get(i).getKey(), filter.get(i).getValue());
            this.filter.add(pair);
        }
        this.instance = new ArrayList<Pair<String, String>>();
        for (int i=0; i<instance.size(); i++) {
            Pair<String, String> pair = new Pair(instance.get(i).getKey(), instance.get(i).getValue());
            this.instance.add(pair);
        }
        this.columns = new ArrayList<String>();
    }

    /**
     * Constructor, specific for delete command
     * @param command
     * @param tableName
     * @param filter
     * @param yesFilter
     */
    public Query(String command, String tableName, List<Pair<String, String>> filter, boolean yesFilter) {
        this.command = command;
        this.tableName = tableName;
        this.filter = new ArrayList<Pair<String, String>>();
        for (int i=0; i<filter.size(); i++) {
            Pair<String, String> pair = new Pair(filter.get(i).getKey(), filter.get(i).getValue());
            this.filter.add(pair);
        }
        this.columns = new ArrayList<String>();
        this.instance = new ArrayList<Pair<String, String>>();
    }

    /**
     * Constructor, specific for read command
     * @param command
     * @param tableName
     * @param columns
     * @param filter
     */
    public Query(String command, String tableName, List<String> columns, List<Pair<String, String>> filter, boolean yesRead) {
        this.command = command;
        this.tableName = tableName;
        this.filter = new ArrayList<Pair<String, String>>();
        for (int i=0; i<filter.size(); i++) {
            Pair<String, String> pair = new Pair(filter.get(i).getKey(), filter.get(i).getValue());
            this.filter.add(pair);
        }
        this.columns = new ArrayList<String>();
        for (int i=0; i<columns.size(); i++) {
            this.columns.add(columns.get(i));
        }
        this.instance = new ArrayList<Pair<String, String>>();
    }

    public String getCommand() {
        return this.command;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public List<Pair<String, String>> getInstance() {
        return this.instance;
    }

    public List<Pair<String, String>> getFilter() {
        return this.filter;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<String> columns) {
        this.columns = new ArrayList<String>();
        for (int i=0; i<columns.size(); i++) {
            this.columns.add(columns.get(i));
        }
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void setInstance(List<Pair<String, String>> instance) {
        this.instance = new ArrayList<Pair<String, String>>();
        for (int i=0; i<instance.size(); i++) {
            Pair<String, String> pair = new Pair(instance.get(i).getKey(), instance.get(i).getValue());
            this.instance.add(pair);
        }
    }

    public void addInstance(Pair<String, String> pair) {
        this.instance.add(pair);
    }

    public void setFilter(List<Pair<String, String>> filter) {
        this.filter = new ArrayList<Pair<String, String>>();
        for (int i=0; i<filter.size(); i++) {
            Pair<String, String> pair = new Pair(filter.get(i).getKey(), filter.get(i).getValue());
            this.filter.add(pair);
        }
    }

    public void addFilter(Pair<String, String> filter) {
        this.filter.add(filter);
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer("");
        if (this.command.equalsIgnoreCase("insert")) {
            str.append("INSERT INTO "+this.tableName);
            str.append(" (");
            for (int i=0; i<this.instance.size(); i++) {
                str.append(this.instance.get(i).getKey());
                if (i<this.instance.size()-1) {
                    str.append(',');
                }
            }
            str.append(") VALUES(");
            for (int i=0; i<this.instance.size(); i++) {
                str.append(this.instance.get(i).getValue());
                if (i<this.instance.size()-1) {
                    str.append(',');
                }
            }
            str.append(");");
        }
        else if (this.command.equalsIgnoreCase("update")) {
            str.append("UPDATE "+this.tableName);
            str.append(" SET ");
            for (int i=0; i<this.instance.size(); i++) {
                str.append(this.instance.get(i).getKey()+"="+this.instance.get(i).getValue());
                if (i<this.instance.size()-1) {
                    str.append(',');
                }
            }
            str.append(" WHERE ");
            for (int i=0; i<this.filter.size(); i++) {
                str.append(this.filter.get(i).getKey()+"="+this.filter.get(i).getValue());
                if (i<this.filter.size()-1) {
                    str.append(',');
                }
            }
            str.append(';');
        }
        else if (this.command.equalsIgnoreCase("delete")) {
            str.append("DELETE FROM "+this.tableName);
            str.append(" WHERE ");
            for (int i=0; i<this.filter.size(); i++) {
                str.append(this.filter.get(i).getKey()+"="+this.filter.get(i).getValue());
                if (i<this.filter.size()-1) {
                    str.append(',');
                }
            }
            str.append(";");
        }
        else if (this.command.equalsIgnoreCase("read")) {
            str.append("SELECT ");
            for (int i=0; i<this.columns.size(); i++) {
                str.append(this.columns.get(i));
                if (i<this.columns.size()-1)
                    str.append(',');
            }
            str.append(" FROM "+this.tableName);
            str.append(" WHERE ");
            for (int i=0; i<this.filter.size(); i++) {
                str.append(this.filter.get(i).getKey()+"="+this.filter.get(i).getValue());
                if (i<this.filter.size()-1) {
                    str.append(',');
                }
            }
            str.append(";");
        }
        return str.toString();
    }
}
