package playtestApp;

import java.util.ArrayList;

import javax.swing.JFrame;

// import java.sql.*;
// import java.util.Vector;

// import javax.swing.table.DefaultTableModel; 

public class DataManager 
{
	public static boolean loggedIn = false;
	private static int userId = 0; 
	private static String username = ""; 
	private static boolean isAdmin = false; 
	
	public static class EntryInfo
	{
		public int id = -1; 
		public String name = "Invalid";
		public Boolean hasInfo = false; 
		
		public EntryInfo(int id, String name)
		{
			this.id = id; 
			this.name = name;
			hasInfo = true; 
		}
		
		public EntryInfo()
		{
			Reset(); 
		}
		
		public void Reset() 
		{
			hasInfo = false; 
			id = -1; 
			name = "Invalid"; 
		}
	}
	
	public static EntryInfo gameInfo = new EntryInfo(); 
	public static ArrayList<EntryInfo> levelInfos = new ArrayList<EntryInfo>();
	public static int levelIndex = 0; 
	
	
	public static void Login(String username, int userId, Boolean isAdmin)
	{
		DataManager.username = username; 
		DataManager.userId = userId;
		DataManager.isAdmin = isAdmin; 
		
		loggedIn = true; 
	}
	
	public static void Logout()
	{
		username = ""; 
		isAdmin = false; 
		
		loggedIn = false; 
	}
	
	public static String GetUsername()
	{
		return username; 
	}

	public static int GetUserId()
	{
		return userId; 
	}
	
	public static Boolean GetAdminStatus()
	{
		return isAdmin; 
	}

	public static void ResetLevelInfos()
	{
		levelInfos.clear(); 
		
//		for (EntryInfo levelInfo : levelInfos) 
//			levelInfo.Reset(); 
	}
	
	public static EntryInfo GetCurrLevel()
	{
		return levelInfos.get(levelIndex); 
	}

	// Write a method NextLevel() which increases the levelIndex. If the levelIndex is greater than the size of the levelInfos array, set the levelIndex to 0.
	public static void NextLevel()
	{
		levelIndex++; 
		
		if (levelIndex >= levelInfos.size())
			levelIndex = 0; 
	}

	// Write a method PrevLevel() which decreases the levelIndex. If the levelIndex is less than 0, set the levelIndex to the size of the levelInfos array - 1.
	public static void PrevLevel()
	{
		levelIndex--; 
		
		if (levelIndex < 0)
			levelIndex = levelInfos.size() - 1; 
	}

	public static void OpenSettings(JFrame parent)
	{
		Settings settings = new Settings(parent); 
		settings.setVisible(true);
		// parent.setVisible(false);
	}
}
