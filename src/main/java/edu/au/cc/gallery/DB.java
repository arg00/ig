package edu.au.cc.gallery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {

	/*
    public DB() {
    	//System.out.println("made new db");
    }
    */
	
	
	/*
    private static final String dbUrl = "jdbc:postgresql://gallery-db.c5qjvlv2tflz.us-west-1.rds.amazonaws.com/image_gallery";

    private Connection connection;
    
    public void connect() throws SQLException {
	
	try {
	    //Class.forName("org.postgresql.Driver");
	    //JSONObject secret = getSecret();
	    connection = DriverManager.getConnection(dbUrl, "image_gallery", "Ziege666");
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
	
    }
	*/
	
	private static final String dbUrl = "jdbc:postgresql://image-gallery-db.c5qjvlv2tflz.us-west-1.rds.amazonaws.com/image_gallery";

    private Connection connection;

    
    private JSONObject getSecret() {
	String s = Secrets.getSecretImageGallery();
	return new JSONObject(s);
    }

    private String getPassword(JSONObject secret) {
	return secret.getString("password");
    }
    
    
    public void connect() throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
			JSONObject secret = getSecret();
			connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword(secret));
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
    }

    public ResultSet executeQuery(String query) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	ResultSet rs = stmt.executeQuery();
	return rs;
    }

    public void execute(String query, String[] values) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	for(int i=0; i < values.length; i++)
	    stmt.setString(i+1, values[i]);
	stmt.execute();
    }

    public ResultSet executeQuery(String query, String[] values) throws SQLException {	
        PreparedStatement stmt = connection.prepareStatement(query);

	for(int i = 0; i < values.length; i++) 
		stmt.setString(i+1, values[i]);
	ResultSet rs = stmt.executeQuery();
	return rs;
    }

    public void close() throws SQLException {
	connection.close();
    }

    public void initDB() throws Exception {
	this.connect();
    }

    public void listAll() throws Exception {
	System.out.println("username\tpassword\tfull name");
	System.out.println("----------------------------------------");
	ResultSet rs = this.executeQuery("select username,password,full_name from users");
	while(rs.next()) System.out.println(rs.getString(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getString(3));
	rs.close();
    }

    public void addUser(String un, String pw, String fn) throws Exception {
	String[] vals = {un, pw, fn};
	String stmt = "insert into users\nvalues (?,?,?)";
	this.execute(stmt, vals);
    }

    public ArrayList<String> getUsers() throws Exception {
	ResultSet rs = this.executeQuery("select username from users");
	ArrayList<String> userList = new ArrayList<String>();
	while(rs.next()) userList.add(rs.getString(1));
	return userList;
    }

    public void updateUser(String un, String pw, String fn) throws Exception {
	String[] val = {un};
	ResultSet rs = this.executeQuery("select * from users where username=?", val);
	ArrayList<String> currentVals = new ArrayList<String>();
	while(rs.next()) { currentVals.add(rs.getString(1)); currentVals.add(rs.getString(2)); currentVals.add(rs.getString(3));  }
	if (pw.equals("")) pw = currentVals.get(1);
	if (fn.equals("")) fn = currentVals.get(2);
	String updtStmt = "update users\nset password = ?, full_name = ?\nwhere username = ?";
	String[] stmtArgs = { pw, fn, un };
	this.execute(updtStmt, stmtArgs);
    }

    public void deleteUser(String un) throws Exception {
	String[] vals = { un };
	this.execute("delete from users where username = ?", vals);
    }

    public ArrayList<String> getUserInfo(String username) throws Exception {
	String[] vals = { username };
	ResultSet rs = this.executeQuery("select * from users where username = ?", vals);
	ArrayList<String> userInfo = new ArrayList<String>();
	while (rs.next()) {
		userInfo.add(username);
		userInfo.add(rs.getString(2));
		userInfo.add(rs.getString(3));
	}
	return userInfo;
    }
}
