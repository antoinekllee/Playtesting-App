package playtestApp;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.sql.*;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ReviewEditor extends JFrame {

	private JPanel contentPane;
	private JTextField txtTimeTaken;

	private int reviewId = 0;
	private Vector<Object> reviewInfo = new Vector<Object>();
	private JComboBox<String> cmbUser;
	private JComboBox<String> cmbGame;
	private JComboBox<String> cmbLevel;
	private JSpinner spnDifficulty;
	private JTextArea txtSuggestions;
	private JTextArea txtBugs;

	private AllReviews parent = null; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReviewEditor frame = new ReviewEditor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ReviewEditor (int reviewId, AllReviews parent)
	{
		this();
		this.reviewId = reviewId; 
		this.parent = parent; 

		GetReview(); 

		// Set the user combo box as having one option, set to the user of the review
		cmbUser.addItem((String)reviewInfo.get(0));

		// Set the game combo box as having one option, set to the game of the review
		cmbGame.addItem((String)reviewInfo.get(1));

		// Set the level combo box as having one option, set to the level of the review
		cmbLevel.addItem((String)reviewInfo.get(2));

		// Set the value of the difficulty spinner to the difficulty of the review. Diffulculty is stored as a string so it must be converted to an int
		spnDifficulty.setValue(Integer.parseInt((String)reviewInfo.get(3)));

		// Set the value of the time taken text field to the time taken of the review
		txtTimeTaken.setText((String)reviewInfo.get(4));

		// Set the value of the suggestions text area to the suggestions of the review
		txtSuggestions.setText((String)reviewInfo.get(5));

		// Set the value of the bugs text area to the bugs of the review
		txtBugs.setText((String)reviewInfo.get(6));
	}

	/**
	 * Create the frame.
	 */
	public ReviewEditor() 
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setAlwaysOnTop(true);

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Edit Review");
		lblTitle.setBounds(117, 29, 86, 16);
		contentPane.add(lblTitle);
		
		JLabel lblUser = new JLabel("User");
		lblUser.setBounds(43, 71, 86, 16);
		contentPane.add(lblUser);
		
		cmbUser = new JComboBox<String>();
		cmbUser.setBounds(141, 67, 119, 27);
		contentPane.add(cmbUser);
		
		cmbGame = new JComboBox<String>();
		cmbGame.setBounds(141, 102, 119, 27);
		contentPane.add(cmbGame);
		
		JLabel lblGame = new JLabel("Game");
		lblGame.setBounds(43, 106, 86, 16);
		contentPane.add(lblGame);
		
		cmbLevel = new JComboBox<String>();
		cmbLevel.setBounds(141, 140, 119, 27);
		contentPane.add(cmbLevel);
		
		JLabel lblLevel = new JLabel("Level");
		lblLevel.setBounds(43, 144, 86, 16);
		contentPane.add(lblLevel);
		
		JLabel lblDifficulty = new JLabel("Difficulty");
		lblDifficulty.setBounds(43, 179, 86, 16);
		contentPane.add(lblDifficulty);
		
		spnDifficulty = new JSpinner();
		spnDifficulty.setBounds(141, 174, 119, 26);
		contentPane.add(spnDifficulty);
		
		JLabel lblTimeTaken = new JLabel("Time Taken");
		lblTimeTaken.setBounds(43, 214, 86, 16);
		contentPane.add(lblTimeTaken);
		
		txtTimeTaken = new JTextField();
		txtTimeTaken.setBounds(141, 209, 119, 26);
		contentPane.add(txtTimeTaken);
		txtTimeTaken.setColumns(10);
		
		JLabel lblSuggestions = new JLabel("Suggestions");
		lblSuggestions.setBounds(43, 255, 86, 16);
		contentPane.add(lblSuggestions);
		
		txtSuggestions = new JTextArea();
		txtSuggestions.setBounds(43, 283, 217, 61);
		contentPane.add(txtSuggestions);
		
		txtBugs = new JTextArea();
		txtBugs.setBounds(43, 384, 217, 61);
		contentPane.add(txtBugs);
		
		JLabel lblBugs = new JLabel("Bugs");
		lblBugs.setBounds(43, 356, 86, 16);
		contentPane.add(lblBugs);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CloseWindow(); 
			}
		});
		btnSave.setBounds(148, 468, 100, 29);
		contentPane.add(btnSave);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteReview();
			}
		});
		btnDelete.setBounds(43, 468, 100, 29);
		contentPane.add(btnDelete);

		setLocationRelativeTo(null);
		ColourManager.globalStyling(this); 
	}

	private void GetReview()
	{
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 

			String sql = "SELECT LevelName, GameID, Username, Difficulty, TimeTaken, Suggestions, Bugs, ReviewID FROM Reviews JOIN Levels ON Reviews.LevelID = Levels.LevelID JOIN Users ON Reviews.UserID = Users.UserID WHERE ReviewID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, reviewId);
			ResultSet rs = stmt.executeQuery();
			
			rs.next(); 
			
			reviewInfo = new Vector<Object>();

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

			reviewInfo.add(user); 
			reviewInfo.add(gameName);
			reviewInfo.add(levelName);
			reviewInfo.add(difficulty);
			reviewInfo.add(timeTaken);
			reviewInfo.add(suggestions);
			reviewInfo.add(bugs);
			reviewInfo.add(rs.getInt("ReviewID"));

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

	private void CloseWindow ()
	{
		this.dispose();
	}

	private void DeleteReview()
	{
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 

			String sql = "DELETE FROM Reviews WHERE ReviewID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, reviewId);
			stmt.executeUpdate();
			
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		parent.Reset(); 
		CloseWindow();
	}
}
