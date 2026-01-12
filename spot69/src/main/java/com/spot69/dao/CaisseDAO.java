package com.spot69.dao;

//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.*;
import java.util.*;
import com.spot69.model.Caisse;
import com.spot69.model.Commande;
import com.spot69.utils.DBConnection;

public class CaisseDAO {
	
	public boolean ajouterCaisse(int idCaissiere, Timestamp openAt, Timestamp closeAt) throws SQLException {
	    Connection con = DBConnection.getConnection();
	    String sql = "INSERT INTO CAISSE (ID_CAISSIERE, OPEN_AT, CLOSE_AT, CRAETE_AT, UPDATE_AT) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
	    
	    PreparedStatement st = con.prepareStatement(sql);
	    st.setInt(1, idCaissiere);
	    st.setTimestamp(2, openAt);
	    st.setTimestamp(3, closeAt);
	    
	    int rowsInserted = st.executeUpdate();
	    st.close();
	    con.close();
	    
	    return rowsInserted > 0;
	}

	
	private ArrayList<Commande> com;

	public Caisse getCaisse(int id_caisse) throws SQLException
	{
		Connection con = DBConnection.getConnection();
		String sql = "SELECT * FROM CAISSE WHERE ID_CAISSIERE = ? ORDER BY OPEN_AT DESC LIMIT 1;";
		
		PreparedStatement st = con.prepareStatement(sql);
		st.setInt(1, id_caisse);
		
//		ResultSet rs = st.executeQuery();
//
//		Caisse caisse = new Caisse(rs.getInt("ID"), rs.getInt("ID_CAISSE"), rs.getTimestamp("OPEN_AT"), rs.getTimestamp("CLOSE_AT"), rs.getTimestamp("CREATE_AT"), rs.getTimestamp("UPDATE_AT"));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
		    return new Caisse(
		        rs.getInt("ID"),
		        rs.getInt("ID_CAISSIERE"), // et non ID_CAISSE
		        rs.getTimestamp("OPEN_AT"),
		        rs.getTimestamp("CLOSE_AT"),
		        rs.getTimestamp("CREATE_AT"),
		        rs.getTimestamp("UPDATE_AT")
		    );
		}
		return null;

//		return caisse;
		
	}
	
	public ArrayList<Commande> getRapport(int id_caisse) throws SQLException {
		
		Commande objCom = new Commande();
		com = new ArrayList<Commande>();
		
		Caisse cs = this.getCaisse(id_caisse);
		
		Connection con = DBConnection.getConnection();
//		String sql = "SELECT *"
//				+ "FROM COMMANDE\r\n"
//				+ "WHERE DATE_COMMANDE BETWEEN"
//				+ open_at
//				+ " AND "
//				+ close_at
//				+ "ORDER BY DATE_COMMANDE DESC;\r\n"
//				+ "";
		String sql = "SELECT * FROM COMMANDE WHERE DATE_COMMANDE BETWEEN ? AND ?";
		
		PreparedStatement st = con.prepareStatement(sql);
		
		Timestamp open_at = cs.getOpen_at();
		Timestamp close_at = cs.getClose_at();
		
		System.out.println("OBJET CAISSE "+cs);
		
		st.setTimestamp(1, open_at);
		st.setTimestamp(2, close_at);
		
		ResultSet rs = st.executeQuery();
		
		while(rs.next())
		{
			objCom.setId(rs.getInt("ID"));
			objCom.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
			objCom.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
			objCom.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
			
			com.add(objCom);
		}
		
		return com;
		
	}
}
