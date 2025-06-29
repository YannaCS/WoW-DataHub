package game.dal;

import java.sql.*;
import java.util.*;

import game.model.*;

public class ClansDao {
	/**
	 * create a new Clans record (clanName + race) in the database
	 * VALIDATES race-clan relationship according to data model requirements
	 * @return a Clans object
	 */
	public static Clans create(
			Connection cxn,
			String clanName,
			Clans.Races race
	) throws SQLException {
		// Validate race-clan relationship according to data model
		if (!isValidRaceClanCombination(clanName, race)) {
			throw new SQLException("Invalid race-clan combination: " + clanName + " cannot be of race " + race);
		}
		
		String insertDegree = "INSERT INTO Clans (clanName, race) VALUES (?, ?);";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(insertDegree)) {
			pstmt.setString(1,  clanName);
			pstmt.setString(2,  race.name().toLowerCase());
			
			pstmt.executeUpdate();
			
			return new Clans(clanName, race);
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate entry")) {
				// If duplicate, return the existing record
				return getClanRacebyClanName(cxn, clanName);
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Validates race-clan combinations according to data model requirements
	 * Each race has exactly two possible clans
	 */
	private static boolean isValidRaceClanCombination(String clanName, Clans.Races race) {
		Map<Clans.Races, Set<String>> validCombinations = Map.of(
			Clans.Races.HUMAN, Set.of("Midlanders", "Highlanders", "Stormwind Alliance", "Kul Tiran Fleet", "Gilnean Pack"),
			Clans.Races.ELF, Set.of("Duskwight", "Wildwood", "Darnassus Sentinels", "Void Elves", "Blood Elves"),
			Clans.Races.DWARF, Set.of("Ironforge Dwarves", "Wildhammer Clan", "Dark Iron Dwarves"),
			Clans.Races.ORC, Set.of("Orgrimmar Horde", "Mag'har Orcs", "Undercity Forsaken"),
			Clans.Races.GOBLIN, Set.of("Bilgewater Cartel", "Darkspear Trolls", "Zandalari Empire")
		);
		
		Set<String> validClansForRace = validCombinations.get(race);
		return validClansForRace != null && validClansForRace.contains(clanName);
	}
	
	/**
	 * Get all valid clan names for a specific race
	 */
	public static Set<String> getValidClansForRace(Clans.Races race) {
		Map<Clans.Races, Set<String>> validCombinations = Map.of(
			Clans.Races.HUMAN, Set.of("Midlanders", "Highlanders", "Stormwind Alliance", "Kul Tiran Fleet", "Gilnean Pack"),
			Clans.Races.ELF, Set.of("Duskwight", "Wildwood", "Darnassus Sentinels", "Void Elves", "Blood Elves"),
			Clans.Races.DWARF, Set.of("Ironforge Dwarves", "Wildhammer Clan", "Dark Iron Dwarves"),
			Clans.Races.ORC, Set.of("Orgrimmar Horde", "Mag'har Orcs", "Undercity Forsaken"),
			Clans.Races.GOBLIN, Set.of("Bilgewater Cartel", "Darkspear Trolls", "Zandalari Empire")
		);
		
		return validCombinations.getOrDefault(race, Set.of());
	}
	
	/**
	 * retrieves a single record based on pk(clanName)
	 * return an Clans object or null if not found
	 */
	public static Clans getClanRacebyClanName(
			Connection cxn,
			String clanName
	) throws SQLException {
		String query_ClanName = """
				SELECT *
				FROM Clans
				WHERE clanName = ? ;
				""";
		try (PreparedStatement pstmt = cxn.prepareStatement(query_ClanName)) {
			pstmt.setString(1, clanName);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Clans(
							clanName,
							Clans.Races.valueOf(rs.getString("race").toUpperCase())
							);
				} else {
					return null;
				}
			}
		}
	}
	
	
	/**
	 * update an existing Clans record (clanName) in the database
	 * returns a Clans object
	 */
	public static Clans updateClanName(
			Connection cxn,
			Clans oldClan,
			String newClanName
	) throws SQLException {
		// Validate new race-clan combination
		if (!isValidRaceClanCombination(newClanName, oldClan.getRace())) {
			throw new SQLException("Invalid race-clan combination: " + newClanName + " cannot be of race " + oldClan.getRace());
		}
		
		String query_updateClanName = """
				UPDATE Clans
				SET clanName = ?
				WHERE clanName = ?;
				""";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(query_updateClanName)) {
			pstmt.setString(1, newClanName);
			pstmt.setString(2, oldClan.getClanName());
			
			pstmt.executeUpdate();
			
			return new Clans(
					newClanName,
					oldClan.getRace()
					);
		}
	}
	
	
	/**
	 * delete an existing Clans record
	 */
	public static void deleteClan(
			Connection cxn,
			Clans clan
	) throws SQLException {
		String deleteClan = "DELETE FROM Clans WHERE clanName = ?;";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(deleteClan)) {
			pstmt.setString(1, clan.getClanName());
			pstmt.executeUpdate();
		}
	}
	
	/*
	 * return a list of clans of a specific race
	 */
	public static List<Clans> getClansbyRace(
			Connection cxn,
			Clans.Races race
	) throws SQLException {
		String query_RaceClan = """
				SELECT *
				FROM Clans
				WHERE race = ?;
				""";
		
		List<Clans> clans = new ArrayList<>();
		
		try (PreparedStatement pstmt = cxn.prepareStatement(query_RaceClan)) {
			pstmt.setString(1, race.name().toLowerCase());
			
			try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                String clanName = rs.getString("clanName");
	                Clans.Races clanRace = Clans.Races.valueOf(rs.getString("race").toUpperCase());
	                clans.add(new Clans(clanName, clanRace));
	            }
	        }
		}
		return clans;
	}

}