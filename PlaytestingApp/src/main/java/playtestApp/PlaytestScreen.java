package playtestApp;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class PlaytestScreen extends JFrame {

	private JPanel contentPane;
	
	private LevelSelection parent = null;
	private DataManager.EntryInfo levelInfo = null; 

	private Boolean reviewExists = false; 

	// Timer
	private Timer timer;
	private Boolean isTimerCounting = false;
	private long startTime = 0;
	private long elapsedTime = 0;
	private JLabel lblTimer;
	private JSlider sliderDifficulty;
	private JLabel lblDifficultyValue;
	private JButton btnTimerPlayPause; // play/pause button

	// Difficulty
	private int difficulty = 0;
	private JTextArea txtSuggestions;
	private JTextArea txtBugs;
	private JLabel lblStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlaytestScreen frame = new PlaytestScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public PlaytestScreen(LevelSelection parent) // first
	{
		this(); 
		this.parent = parent; 
	}
	
	/**
	 * Create the frame.
	 */
	public PlaytestScreen() 
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		levelInfo = DataManager.GetCurrLevel(); 
		
		JLabel lblTitle = new JLabel(DataManager.gameInfo.name + " - " + levelInfo.name); 
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(294, 54, 228, 16);
		contentPane.add(lblTitle);
		
		JButton btnLevels = new JButton("<-");
		btnLevels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				OpenLevelsScreen(); 
			}
		});
		btnLevels.setBounds(54, 28, 55, 29);
		contentPane.add(btnLevels);
		
		JButton btnPrevLevel = new JButton("<");
		btnPrevLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DataManager.PrevLevel();
				PlaytestScreen newPlaytestScreen = new PlaytestScreen(parent); 
				newPlaytestScreen.setVisible(true);
				setVisible(false);
			}
		});
		btnPrevLevel.setBounds(185, 49, 35, 29);
		contentPane.add(btnPrevLevel);
		
		JButton btnNextLevel = new JButton(">");
		btnNextLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DataManager.NextLevel(); 
				PlaytestScreen newPlaytestScreen = new PlaytestScreen(parent); 
				newPlaytestScreen.setVisible(true);
				setVisible(false);
			}
		});
		btnNextLevel.setBounds(579, 49, 35, 29);
		contentPane.add(btnNextLevel);
		
		lblTimer = new JLabel("00:00:00");
		lblTimer.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		lblTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimer.setBounds(185, 108, 161, 51);
		contentPane.add(lblTimer);
		
		btnTimerPlayPause = new JButton("PLAY");
		btnTimerPlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				ToggleTimer();
			}
		});
		btnTimerPlayPause.setBounds(330, 124, 88, 29);
		contentPane.add(btnTimerPlayPause);
		
		JButton btnTimerEdit = new JButton("EDIT");
		btnTimerEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create a new instance of TimerEditor and pass in the current time, then set visible
				TimerEditor timerEditor = new TimerEditor(elapsedTime, PlaytestScreen.this);
				timerEditor.setVisible(true);
				if (isTimerCounting)
					ToggleTimer();

				// Stop PlaytestScreen window from being interacted with while TimerEditor is open
				setEnabled(false);
			}
		});
		btnTimerEdit.setBounds(426, 124, 88, 29);
		contentPane.add(btnTimerEdit);
		
		sliderDifficulty = new JSlider();
		sliderDifficulty.setValue(5);
		sliderDifficulty.setMinorTickSpacing(1);
		sliderDifficulty.setMajorTickSpacing(5);
		sliderDifficulty.setSnapToTicks(true);
		sliderDifficulty.setPaintLabels(true);
		sliderDifficulty.setPaintTicks(true);
		sliderDifficulty.setMaximum(10);
		sliderDifficulty.setBounds(345, 178, 184, 43);
		sliderDifficulty.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) { 
				UpdateDifficulty(sliderDifficulty.getValue()); 
			}
		});

		UIManager.put("Slider.foreground", Color.decode(ColourManager.colourText));

		// Set the line color of the slider
		UIManager.put("Slider.trackColor", Color.decode(ColourManager.colourText));

		// Set the color of the knob/slider of the slider
		UIManager.put("Slider.thumb", Color.decode(ColourManager.colourText));

		// Refresh the UI to apply the changes
		SwingUtilities.updateComponentTreeUI(sliderDifficulty);
		
		// Set the default value of sliderDifficulty to 5
		// sliderDifficulty.setValue(5);
		contentPane.add(sliderDifficulty);
		
		JLabel lblDifficulty = new JLabel("Difficulty Rating");
		lblDifficulty.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDifficulty.setBounds(185, 190, 133, 16);
		contentPane.add(lblDifficulty);
		
		lblDifficultyValue = new JLabel("10");
		lblDifficultyValue.setBounds(568, 190, 61, 16);
		contentPane.add(lblDifficultyValue);
		
		txtSuggestions = new JTextArea();
		txtSuggestions.setBounds(188, 273, 184, 126);
		contentPane.add(txtSuggestions);
		
		txtBugs = new JTextArea();
		txtBugs.setBounds(430, 273, 184, 126);
		contentPane.add(txtBugs);
		
		JLabel lblSuggestions = new JLabel("Suggestions");
		lblSuggestions.setBounds(188, 245, 140, 16);
		contentPane.add(lblSuggestions);
		
		JLabel lblBugs = new JLabel("Bugs");
		lblBugs.setBounds(430, 245, 61, 16);
		contentPane.add(lblBugs);
		
		JButton btnReset = new JButton("RESET");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (isTimerCounting)
					ToggleTimer();

				SetTimer(0);
			}
		});
		btnReset.setBounds(526, 124, 88, 29);
		contentPane.add(btnReset);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaveReview();  
			}
		});
		btnSave.setBounds(345, 421, 117, 29);
		contentPane.add(btnSave);
		
		lblStatus = new JLabel("");
		lblStatus.setBounds(474, 434, 107, 16);
		contentPane.add(lblStatus);

		setLocationRelativeTo(null);
		ColourManager.globalStyling(this); 

		timer = new Timer(10, new TimerListener());

		CheckExistingReview(); 
	}
	
	private void OpenLevelsScreen() 
	{
//		DataManager.levelInfo.Reset(); 
		parent.setVisible(true);
		this.setVisible(false);
	}

	// Update a stopwatch that is counting up every millisecond, using the Java javax.swing.Timer library
	private void ToggleTimer()
	{
		if (!isTimerCounting) {
			startTime = System.currentTimeMillis() - elapsedTime;
			timer = new Timer(10, new TimerListener());
			timer.start();
			isTimerCounting = true;

			btnTimerPlayPause.setText("PAUSE");
		}
		else
		{
			timer.stop();
			elapsedTime = System.currentTimeMillis() - startTime;
			isTimerCounting = false;

			btnTimerPlayPause.setText("PLAY");
		}
	}

	// Set the value of the timer to a specific elapsed time and update the label
	public void SetTimer(long elapsedTime)
	{
		this.elapsedTime = elapsedTime;
		int minutes = (int) (elapsedTime / 60000);
		int seconds = (int) ((elapsedTime % 60000) / 1000);
		int milliseconds = (int) (elapsedTime % 1000);
		String time = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds / 10);
		lblTimer.setText(time);
	}

	private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            elapsedTime = System.currentTimeMillis() - startTime;
            int minutes = (int) (elapsedTime / 60000);
            int seconds = (int) ((elapsedTime % 60000) / 1000);
            int milliseconds = (int) (elapsedTime % 1000);
            String time = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds / 10);
            lblTimer.setText(time);
        }
    }

	private void UpdateDifficulty(int difficulty) 
	{
		this.difficulty = difficulty; 
		lblDifficultyValue.setText(String.valueOf(difficulty));
	}

	private void SaveReview ()
	{
		if (isTimerCounting)
			ToggleTimer();

		// If the review already exists, update it instead of inserting a new one
		if (reviewExists)
		{
			UpdateReview();
			return;
		}

		int difficulty = this.difficulty; 
		// Convert elapsed time to seconds
		float timeTaken = (float)elapsedTime / 1000;
		// Round time taken to 2 dp
		timeTaken = (float)Math.round(timeTaken * 100) / 100;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(timeTaken);
		String suggestions = txtSuggestions.getText();
		String bugs = txtBugs.getText(); 

		// Save the review to the database 
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to insert the review into the database reviews table
			// It should have level_id, user_id, difficulty, time_taken, suggestions, and bugs
			String query = "INSERT INTO Reviews (LevelID, UserID, Difficulty, TimeTaken, Suggestions, Bugs) VALUES (?, ?, ?, ?, ?, ?)";

			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelInfo.id);
			stmt.setInt(2, DataManager.GetUserId());
			stmt.setInt(3, difficulty);
			stmt.setFloat(4, timeTaken); 
			stmt.setString(5, suggestions);
			stmt.setString(6, bugs);

			// Execute the query
			stmt.executeUpdate();

			lblStatus.setText("Saved!");

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// Update an existing review in the database
	private void UpdateReview()
	{
		int difficulty = this.difficulty; 
		// Convert elapsed time to seconds
		float timeTaken = (float)elapsedTime / 1000;
		// Round time taken to 2 dp
		timeTaken = (float)Math.round(timeTaken * 100) / 100;
		System.out.println(timeTaken);
		String suggestions = txtSuggestions.getText();
		String bugs = txtBugs.getText(); 

		// Save the review to the database 
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to update the review in the database reviews table
			// It should have difficulty, time_taken, suggestions, and bugs
			String query = "UPDATE Reviews SET Difficulty = ?, TimeTaken = ?, Suggestions = ?, Bugs = ? WHERE LevelID = ? AND UserID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, difficulty);
			stmt.setFloat(2, timeTaken); 
			stmt.setString(3, suggestions);
			stmt.setString(4, bugs);
			stmt.setInt(5, levelInfo.id);
			stmt.setInt(6, DataManager.GetUserId());

			// Execute the query
			stmt.executeUpdate();

			lblStatus.setText("Updated!");

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// Check if the user has already reviewed this level
	private void CheckExistingReview()
	{
		System.out.println("Checking existing review");
		
		Connection conn; 

		try
		{
			conn = DatabaseManager.getConnection(); 
			
			// Write a query string and a prepared statement to select the review from the database reviews table
			// It should have level_id, user_id, difficulty, time_taken, suggestions, and bugs
			String query = "SELECT * FROM Reviews WHERE LevelID = ? AND UserID = ?";
			PreparedStatement stmt = conn.prepareStatement(query);

			// Set the values of the prepared statement
			stmt.setInt(1, levelInfo.id);
			stmt.setInt(2, DataManager.GetUserId());

			// Execute the query
			ResultSet rs = stmt.executeQuery();

			// If the result set has a row, then the user has already reviewed this level
			if (rs.next())
			{
				// Get the values from the result set
				int difficulty = rs.getInt("Difficulty");
				float timeTaken = rs.getFloat("TimeTaken");
				String suggestions = rs.getString("Suggestions");
				String bugs = rs.getString("Bugs");

				// Set the values of the UI elements
				sliderDifficulty.setValue(difficulty);
				lblDifficultyValue.setText(String.valueOf(difficulty));
				txtSuggestions.setText(suggestions);
				txtBugs.setText(bugs);

				// Convert the time taken to milliseconds
				long timeTakenMillis = (long)(timeTaken * 1000);
				// Set the timer to the time taken
				elapsedTime = timeTakenMillis;
				int minutes = (int) (elapsedTime / 60000);
				int seconds = (int) ((elapsedTime % 60000) / 1000);
				int milliseconds = (int) (elapsedTime % 1000);
				String time = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds / 10);
				lblTimer.setText(time);

				// Set the status label
//				lblStatus.setText("Level has already been reviewed!");

				System.out.println("Review already exists!");
				reviewExists = true; 
			}

			// Close the connection
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
