package playtestApp;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LevelEditor extends JFrame {

	private JPanel contentPane;

	private LevelSelection parent = null;
	private DataManager.EntryInfo levelInfo = null; 
	private JTextField txtName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LevelEditor frame = new LevelEditor(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LevelEditor(LevelSelection parent, Boolean isEdit)
	{
		this(isEdit);
		this.parent = parent; 
	}

	/**
	 * @wbp.parser.constructor
	 */
	public LevelEditor(Boolean isEdit) 
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setAlwaysOnTop(true);

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		if (isEdit)
			levelInfo = DataManager.GetCurrLevel(); 
		
		JLabel lblTitle = new JLabel("Edit Level Info");
		lblTitle.setBounds(197, 75, 149, 16);
		contentPane.add(lblTitle);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(124, 108, 61, 16);
		contentPane.add(lblName);
		
		txtName = new JTextField();
		txtName.setBounds(197, 103, 130, 26);
		contentPane.add(txtName);
		txtName.setColumns(10);
		// Set the text of txtName to the name of the level
		txtName.setText(isEdit ? levelInfo.name : "New Level");
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteLevel(); 
			}
		});
		btnDelete.setBounds(197, 183, 101, 29);
		contentPane.add(btnDelete);
		// Hide delete button if isEdit is false
		btnDelete.setVisible(isEdit);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isEdit) 
					SaveLevelInfo(); 
				else
					NewLevel();
			}
		});
		btnSave.setBounds(197, 149, 101, 29);
		contentPane.add(btnSave);
		
		JButton btnCancel = new JButton("x");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CloseWindow();
			}
		});
		btnCancel.setBounds(83, 45, 45, 29);
		contentPane.add(btnCancel);

		setLocationRelativeTo(null);
		ColourManager.globalStyling(this); 
	}

	private void DeleteLevel()
	{
		// First, store the LevelIndex of the level to be deleted in a variable
		int levelIndex = -1; 

		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to get the LevelIndex of the level to be deleted
			String query = "SELECT LevelIndex FROM Levels WHERE LevelID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelInfo.id);

			// Execute the query
			ResultSet rs = stmt.executeQuery();

			// Get the LevelIndex from the result set
			rs.next();
			levelIndex = rs.getInt("LevelIndex");

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}


		// Delete the level from the database
		// Delete all the reviews for the level, with the LevelID = levelInfo.id

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to delete the level from the database levels table
			// It should have level_id
			String query = "DELETE FROM Levels WHERE LevelID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelInfo.id);

			// Execute the query
			stmt.executeUpdate();

			// Write a query string and a prepared statement to delete the reviews from the database reviews table
			// It should have level_id
			query = "DELETE FROM Reviews WHERE LevelID = ?";
			stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelInfo.id);

			// Execute the query
			stmt.executeUpdate();

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// Update the LevelIndex of all the levels with a LevelIndex greater than the deleted level's LevelIndex
		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to update the LevelIndex of all the levels with a LevelIndex greater than the deleted level's LevelIndex
			String query = "UPDATE Levels SET LevelIndex = LevelIndex - 1 WHERE LevelIndex > ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelIndex);

			// Execute the query
			stmt.executeUpdate();

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		CloseWindow();
	}

	private void SaveLevelInfo()
	{
		// Save the level info to the database
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to update the level name in the database levels table
			String query = "UPDATE Levels SET LevelName = ? WHERE LevelID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setString(1, txtName.getText());
			stmt.setInt(2, levelInfo.id);

			// Execute the query
			stmt.executeUpdate();

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		CloseWindow();
	}

	private void CloseWindow() {
		// Close the window
		parent.ShowLevels();
		parent.setVisible(true);

		this.setVisible(false);
		this.dispose();
	}

	private int GetNextLevelIndex ()
	{
		// Search through all levels with GameID = DataManager.gameInfo.id and find the highest level index
		// Set the level index to the highest level index + 1

		Connection conn;

		try
		{
			conn = DatabaseManager.getConnection(); 

			// Write a query string and a prepared statement to get the highest level index from the database levels table
			String query = "SELECT MAX(LevelIndex) FROM Levels WHERE GameID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, DataManager.gameInfo.id);

			// Execute the query
			ResultSet rs = stmt.executeQuery();

			// Get the highest level index
			int highestLevelIndex = rs.getInt(1);

			// Close the connection
			conn.close();

			return highestLevelIndex + 1; 
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return -1; 
	}

	private void NewLevel ()
	{
		int levelIndex = GetNextLevelIndex();

		System.out.println(DataManager.gameInfo.id);
		System.out.println(txtName.getText());
		System.out.println(levelIndex);

		// Create a new level in the database
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to insert the level into the database levels table
			String query = "INSERT INTO Levels (GameID, LevelName, LevelIndex) VALUES (?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, DataManager.gameInfo.id);
			stmt.setString(2, txtName.getText());
			stmt.setInt(3, levelIndex);

			// Execute the query
			stmt.executeUpdate();

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		CloseWindow();
	}
}
