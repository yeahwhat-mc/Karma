package mn.frd.yeahKarma.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 20.12.13 20:02
 */
public class PooledConnection {
    private Connection connection = null;
    private MySQLDatabase mySQLDatabase;

    public PooledConnection(MySQLDatabase mySQLDatabase) {
        this.mySQLDatabase = mySQLDatabase;
    }

    public void connect(String hostname, String portnmbr, String database, String username, String password) {
        String url = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            url = "jdbc:mysql://" + hostname + ":" + portnmbr + "/" + database;
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.print("Could not connect to MySQL server!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.print("JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public Connection getSQLConnection() {
        return connection;
    }

    public void close() {
        this.mySQLDatabase.resetPooledConnection(this);
    }

    /**
     * Query the database
     *
     * @param query the database query
     * @return ResultSet of the query
     *
     * @throws SQLException
     * */
    public ResultSet query(String query) throws SQLException, Exception {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            return result;
        } catch (SQLException e) {
            if (e.getMessage().equals("Can not issue data manipulation statements with executeQuery().")) {
                try {
                    statement.executeUpdate(query);
                } catch (SQLException ex) {
                    if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                        String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
                        temp = temp.substring(0, temp.lastIndexOf("'"));
                        throw new SQLException(temp);
                    } else {
                        throw new Exception();
                    }
                }
            } else if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
                temp = temp.substring(0, temp.lastIndexOf("'"));
                throw new SQLException(temp);
            } else {
                throw new Exception();
            }
        }

        return null;
    }

    /**
     * Empties a table
     *
     * @param table the table to empty
     * @return true if data-removal was successful.
     *
     * */
    public boolean clearTable(String table) {
        Statement statement = null;
        String query = null;

        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + table);
            if (result == null)
                return false;
            query = "DELETE FROM " + table;
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Insert data into a table
     *
     * @param table the table to insert data
     * @param column a String[] of the columns to insert to
     * @param value a String[] of the values to insert into the column (value[0] goes in column[0])
     *
     * @return true if insertion was successful.
     * */
    public boolean insert(String table, String[] column, String[] value) {
        Statement statement = null;
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (String s : column) {
            sb1.append(s + ",");
        }
        for (String s : value) {
            sb2.append("'"+s + "',");
        }
        String columns = sb1.toString().substring(0, sb1.toString().length() - 1);
        String values = sb2.toString().substring(0, sb2.toString().length() - 1);
        try {
            statement = this.connection.createStatement();
            statement.execute("INSERT INTO " + table + "(" + columns + ") VALUES (" + values + ")");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a table
     *
     * @param table the table to delete
     * @return true if deletion was successful.
     * */
    public boolean deleteTable(String table) {
        try {
            if (table.equals("") || table == null) {
                return true;
            }

            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE " + table);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String autoComplete(String name){
        String exists = null;
        String query = "SELECT * FROM Player";
        int matches = 0;
        try{
            ResultSet result = query(query);
            while(result.next()){
                String playername = result.getString("name").toLowerCase();
                if(playername.startsWith(name.toLowerCase())){

                    exists = result.getString("name");
                    if(exists.equalsIgnoreCase(name))
                        break;
                    matches++;
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        if(matches > 1 && !exists.equalsIgnoreCase(name))
            return null;
        return exists;
    }

    public boolean transaction(String name, String admin, int value, int cooldown, String reason) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

        try{
            PreparedStatement selectPlayer = connection.prepareStatement("SELECT * FROM Player WHERE name=?");
            selectPlayer.setString(1, name);
            ResultSet result = selectPlayer.executeQuery();
            result.next();
            int newKarma = result.getInt("karma") + value;
            int id = result.getInt("id");

            PreparedStatement transaction = connection.prepareStatement("SELECT * FROM Transaction WHERE player=?");
            transaction.setInt(1, id);
            ResultSet transresult = transaction.executeQuery();

            Timestamp lastTime = null ;
            while(transresult.next()) {
                if(lastTime == null){
                    lastTime = transresult.getTimestamp("timestamp");
                } else {
                    if(lastTime.before(transresult.getTimestamp("timestamp")))
                        lastTime = transresult.getTimestamp("timestamp");
                }
            }


            if(lastTime == null || (ts.getTime() - lastTime.getTime() > cooldown * 1000)){
                PreparedStatement update = connection.prepareStatement("UPDATE Player SET karma=? WHERE id=?");
                update.setInt(1, newKarma);
                update.setInt(2, id);
                update.executeUpdate();

                PreparedStatement transactionInsert = connection.prepareStatement("INSERT INTO  Transaction (player,admin,timestamp,reason,value) VALUES (?, ?, ?, ?, ?)");
                transactionInsert.setInt(1, id);
                transactionInsert.setString(2, admin);
                transactionInsert.setTimestamp(3, ts);
                transactionInsert.setString(4, reason);
                transactionInsert.setInt(5, value);
                transactionInsert.executeUpdate();
            } else {
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        } finally {
            close();
        }

        return true;
    }

    public int getKarma(String name){
        try{
            PreparedStatement selectPlayer = connection.prepareStatement("SELECT * FROM Player WHERE name=?");
            selectPlayer.setString(1, name);
            ResultSet result = selectPlayer.executeQuery();
            result.next();

            return result.getInt("karma");
        }catch (Exception e){
            return 0;
        }

    }
}
