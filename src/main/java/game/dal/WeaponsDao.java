package game.dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.model.*;

public class WeaponsDao {
	private WeaponsDao() {}
	
	public static Weapons create(
			Connection cxn,
			String itemName,
			int level,
			int maxStackSize,
			BigDecimal price,
			int requiredLevel,
			String job,
			int damage
			) throws SQLException {
		String insertWeapons = """
				INSERT INTO Weapons (itemID, wearableJob, damage)
				VALUES (?,?,?);
				""";
		
		try (PreparedStatement insertStmt = cxn.prepareStatement(insertWeapons)) {
			int itemID = EquipmentsDao.create(cxn,itemName,level,maxStackSize,price,requiredLevel);
			insertStmt.setInt(1, itemID);
			insertStmt.setString(2, job);
			insertStmt.setInt(3, damage);
			insertStmt.executeUpdate();
			
			return new Weapons(
					itemID,
					itemName,
					level,
					maxStackSize,
					price,
					requiredLevel,
					job,
					damage);
		}
	}
	
	public static Weapons getWeaponByItemID(Connection cxn, int itemID) throws SQLException {
	    String selectWeapon = """
	            SELECT I.itemID,
	                   I.itemName,
	                   I.level,
	                   I.maxStackSize,
	                   I.price,
	                   E.requiredLevel,
	                   W.wearableJob,
	                   W.damage
	            FROM Weapons W
	            JOIN Equipments E ON W.itemID = E.itemID
	            JOIN Items I ON E.itemID = I.itemID
	            WHERE W.itemID = ?;
	            """;
	    
	    try (PreparedStatement selectStmt = cxn.prepareStatement(selectWeapon)) {
	        selectStmt.setInt(1, itemID);
	        
	        try (ResultSet result = selectStmt.executeQuery()) {
	            if (result.next()) {
	                return new Weapons(
	                        result.getInt("itemID"),
	                        result.getString("itemName"),
	                        result.getInt("level"),
	                        result.getInt("maxStackSize"),
	                        result.getBigDecimal("price"),
	                        result.getInt("requiredLevel"),
	                        result.getString("wearableJob"),
	                        result.getInt("damage"));
	            } else {
	                return null;
	            }
	        }
	    }
	}

	public static Weapons getWeaponByWeaponName(Connection cxn, String itemName) throws SQLException {
	    String selectWeapon = """
	            SELECT I.itemID,
	                   I.itemName,
	                   I.level,
	                   I.maxStackSize,
	                   I.price,
	                   E.requiredLevel,
	                   W.wearableJob,
	                   W.damage
	            FROM Weapons W
	            JOIN Equipments E ON W.itemID = E.itemID
	            JOIN Items I ON E.itemID = I.itemID
	            WHERE I.itemName = ?;
	            """;
	    
	    try (PreparedStatement selectStmt = cxn.prepareStatement(selectWeapon)) {
	        selectStmt.setString(1, itemName);
	        
	        try (ResultSet result = selectStmt.executeQuery()) {
	            if (result.next()) {
	                return new Weapons(
	                        result.getInt("itemID"),
	                        result.getString("itemName"),
	                        result.getInt("level"),
	                        result.getInt("maxStackSize"),
	                        result.getBigDecimal("price"),
	                        result.getInt("requiredLevel"),
	                        result.getString("wearableJob"),
	                        result.getInt("damage"));
	            } else {
	                return null;
	            }
	        }
	    }
	}

	public static List<Weapons> getWeaponsByCharacter(Connection cxn, int charid) throws SQLException{
	    String selectWeaponsOfChar = """
	            SELECT W.itemID,
	                   I.itemName,
	                   I.level,
	                   I.maxStackSize,
	                   I.price,
	                   E.requiredLevel,
	                   W.wearableJob,
	                   W.damage
	            FROM Inventory T
	            JOIN Weapons W ON W.itemID = T.instance
	            JOIN Equipments E ON W.itemID = E.itemID
	            JOIN Items I ON E.itemID = I.itemID
	            WHERE T.charID = ?;
	            """;

	    List<Weapons> weapons = new ArrayList<>();

	    try (PreparedStatement ps = cxn.prepareStatement(selectWeaponsOfChar)) {
	        ps.setInt(1, charid); 

	        try (ResultSet result = ps.executeQuery()) {
	            while (result.next()) {
	                Weapons weapon = new Weapons(
	                        result.getInt("itemID"),
	                        result.getString("itemName"),
	                        result.getInt("level"),
	                        result.getInt("maxStackSize"),
	                        result.getBigDecimal("price"),
	                        result.getInt("requiredLevel"),
	                        result.getString("wearableJob"),
	                        result.getInt("damage"));
	                weapons.add(weapon);
	            }
	            return weapons;
	        }
	    }
	}

	public static List<Weapons> getAllWeapons(Connection cxn) throws SQLException {
	    String selectAllWeapons = """
	            SELECT W.itemID,
	                   I.itemName,
	                   I.level,
	                   I.maxStackSize,
	                   I.price,
	                   E.requiredLevel,
	                   W.wearableJob,
	                   W.damage
	            FROM Weapons W
	            JOIN Equipments E ON W.itemID = E.itemID
	            JOIN Items I ON E.itemID = I.itemID
	            ORDER BY I.itemName;
	            """;
	    
	    List<Weapons> weapons = new ArrayList<>();
	    
	    try (PreparedStatement selectStmt = cxn.prepareStatement(selectAllWeapons)) {
	        try (ResultSet result = selectStmt.executeQuery()) {
	            while (result.next()) {
	                Weapons weapon = new Weapons(
	                        result.getInt("itemID"),
	                        result.getString("itemName"),
	                        result.getInt("level"),
	                        result.getInt("maxStackSize"),
	                        result.getBigDecimal("price"),
	                        result.getInt("requiredLevel"),
	                        result.getString("wearableJob"),
	                        result.getInt("damage"));
	                weapons.add(weapon);
	            }
	        }
	    }
	    return weapons;
	}

}