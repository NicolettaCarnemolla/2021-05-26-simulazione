package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.yelp.model.Archi;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	//Primo punto:
	public List<String> getAllCities(){
		String sql = "SELECT DISTINCT city "
				+ "FROM business "
				+ "ORDER BY city";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String city = res.getString("city");
				result.add(city);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Secondo punto:
	public List<Business> getAllBusinesswithcondition(int anno, String citta){
		String sql = "SELECT * "
				+ "FROM business "
				+ "WHERE city = ? "
				+ "AND ( "
				+ "   SELECT COUNT(*) "
				+ "	  FROM reviews "
				+ "   WHERE business.business_id = reviews.business_id "
				+ "   AND YEAR(review_date) = ? "
				+ ") > 0 "
				+"ORDER BY business_name ASC";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(2, anno);
			st.setString(1, citta);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						citta,
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
				
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Terzo punto; I cammini del grafo: (Utilizzo la classe Archi)
	public List<Archi> getAllArchi(int anno,String citta){
		String sql = "SELECT b1.business_id AS idB1, b2.business_id AS idB2, AVG(r2.stars)-AVG(r1.stars) AS differenza "
				+ "FROM reviews r1, reviews r2, business b1, business b2 "
				+ "WHERE r1.business_id=b1.business_id AND r2.business_id=b2.business_id AND b1.business_id <> b2.business_id AND YEAR(r1.review_date)=YEAR(r2.review_date) AND YEAR(r1.review_date)=? AND b1.city=b2.city AND b1.city=? "
				+ "GROUP BY b1.business_id, b2.business_id "
				+ "HAVING differenza>0";
		List<Archi> result = new ArrayList<Archi>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, citta);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Archi a = new Archi(res.getString("idB1"),res.getString("idB2"),res.getDouble("differenza"));
				result.add(a);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * SELECT distinct b.* "
				+ "FROM business b, reviews r "
				+ "WHERE YEAR(r.review_date) = ? AND b.city = ? AND b.business_id=r.business_id
	 */
}
