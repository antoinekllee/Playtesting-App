package playtestApp;

import java.awt.EventQueue;
import java.sql.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;

public class AllReviews extends JFrame {

	private JPanel contentPane;

	private JTable tblReviews = null; 
	private JComboBox<String> cmbField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AllReviews frame = new AllReviews();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AllReviews() 
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Reviews");
		lblTitle.setBounds(388, 20, 61, 16);
		contentPane.add(lblTitle);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(43, 69, 714, 224);
		contentPane.add(scrollPane);
		
		tblReviews = new JTable();
		scrollPane.setViewportView(tblReviews);
		
		JLabel lblFilter = new JLabel("Filter");
		lblFilter.setBounds(50, 317, 61, 16);
		contentPane.add(lblFilter);
		
		JLabel lblField = new JLabel("Field");
		lblField.setBounds(43, 350, 61, 16);
		contentPane.add(lblField);
		
		cmbField = new JComboBox<String>();
		cmbField.setBounds(84, 345, 117, 27);
		contentPane.add(cmbField);
		// Set options for each of the following: "User", "Game", "Level", "Difficulty", "TimeTaken"
		cmbField.addItem("User");
		cmbField.addItem("Game");
		cmbField.addItem("Level");
		cmbField.addItem("Difficulty");
		cmbField.addItem("TimeTaken");
		
		ShowReviews(); 
	}

	private void ShowReviews()
	{
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 

			// Query the Reviews table in the database and output all the reviews to the tblReviews table
			// The columns are LevelID, UserID, Difficulty, TimeTaken, Suggestions and Bugs
			// Join the Reviews table with the Levels table to get the LevelName
			// Join the Reviews table with the Users table to get the Username

			String sql = "SELECT LevelName, GameID, Username, Difficulty, TimeTaken, Suggestions, Bugs FROM Reviews JOIN Levels ON Reviews.LevelID = Levels.LevelID JOIN Users ON Reviews.UserID = Users.UserID";
			// String sql = "SELECT * FROM Reviews";

			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			DefaultTableModel model = new DefaultTableModel(new String[]{"User", "Game", "Level", "Difficulty", "TimeTaken", "Suggestions", "Bugs"}, 0);
			
			while (rs.next())
			{
				String user = rs.getString("Username");

				String levelName = rs.getString("LevelName"); 
				int gameId = rs.getInt("GameID");
				String gameName = GetGame(gameId);

				String difficulty = rs.getString("Difficulty");
				String timeTaken = rs.getString("TimeTaken");
				String suggestions = rs.getString("Suggestions");
				String bugs = rs.getString("Bugs");

				if (suggestions.equals(""))
					suggestions = "-";

				if (bugs.equals(""))
					bugs = "-";

				model.addRow(new Object[]{user, gameName, levelName, difficulty, timeTaken, suggestions, bugs});
			}

			tblReviews.setModel(model);

			conn.close();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private String GetGame (int gameId)
	{
		Connection conn; 

		try 
		{
			conn = DatabaseManager.getConnection();
			
			String sql = "SELECT Name FROM Games WHERE GameID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, gameId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next())
			{
				String gameName = rs.getString("Name");
				return gameName;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return "NOT FOUND";
	}
}
