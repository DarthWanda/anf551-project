package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;


public class MySQLConnection  {

	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	public boolean setLock(String lockID, int status) {
		if(conn == null) {
			System.err.println("DB connection error");
			return false;
		}

		String sql = "UPDATE locks SET status = ? WHERE item_id = ?" ;
		PreparedStatement ps;
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setString(2, lockID);
			ps.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;


	}


//	public void unsetLock() {
//		if(conn == null) {
//			System.err.println("DB connection error");
//			return;
//		}
//
//		String sql = "UPDATE lock SET status = 0 WHERE item_id = '8888" ;
//		PreparedStatement ps;
//		try {
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


	public String getFullname(String userId) {
		if (conn == null){
			return null;
		}
		String name = "";
		try{
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return name;
	}


	public boolean verifyLogin(String userId, String password) {
		if(conn == null) {
			return false;
		}

		try {
			String sql = "SELECT * from users WHERE user_id = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}
	
	public boolean registerUser(String userId, String password, String firstname, String lastname){
		if(conn == null){
			System.err.println("DB connection failed");
			return false;
		}
		
		try{
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

}
