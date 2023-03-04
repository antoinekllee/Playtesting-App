package playtestApp;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class Login extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername;
    // private JTextField txtPassword;
    private JPasswordField txtPassword;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
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
    public Login() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("Login");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(368, 137, 61, 16);
        contentPane.add(lblTitle);

        txtUsername = new JTextField();
        txtUsername.setBounds(336, 176, 130, 26);
        contentPane.add(txtUsername);
        txtUsername.setColumns(10);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUsername.setBounds(211, 181, 99, 16);
        contentPane.add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPassword.setBounds(211, 219, 99, 16);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setColumns(10);
        txtPassword.setBounds(336, 214, 130, 26);
        contentPane.add(txtPassword);

        // txtPassword = new JPasswordField();
		// txtPassword.setBounds(481, 214, 155, 26);
		// contentPane.add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AttemptLogin(txtUsername.getText(), txtPassword.getText());
            }
        });
        btnLogin.setBounds(353, 271, 99, 29);
        contentPane.add(btnLogin);
        
        JButton btnMenu = new JButton("Menu");
        btnMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OpenMenu(); 
        	}
        });
        btnMenu.setBounds(43, 39, 93, 29);
        contentPane.add(btnMenu);

        ImageIcon icon = new ImageIcon(Menu.class.getResource("/images/background.jpg")); 
		Image image = icon.getImage(); 
    	Image newImg = image.getScaledInstance(800, 500, Image.SCALE_SMOOTH); 
    	icon = new ImageIcon(newImg); 
		JLabel lblImage = new JLabel("");
		lblImage.setBounds(0, 0, 800, 472);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setIcon(icon); 
		contentPane.add(lblImage);

        setLocationRelativeTo(null);
		ColourManager.globalStyling(this); 
    }

    private void AttemptLogin (String username, String password)
    {
        System.out.println("ATTEMPTING LOG IN AS " + username);

        Connection conn;

        try
        {
            conn = DatabaseManager.getConnection();

            String query = "SELECT * FROM Users WHERE Username=? and Password=?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet res = stmt.executeQuery();

            if(res.next())
            {
                Boolean isAdmin = res.getInt("IsAdmin") == 1;
                System.out.println("LOGGING IN AS " + (isAdmin ? "ADMIN" : "NORMAL") + " USER " + username);

                // Get the UserID for this user from the database
                int userID = res.getInt("UserID");

                DataManager.Login(username, userID, isAdmin);

                GameSelection gameSelection = new GameSelection();
                gameSelection.setVisible(true);
                setVisible(false);
            }
            else
                System.out.println("USERNAME OR PASSWORD INCORRECT");
            
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void OpenMenu()
    {
        Menu menu = new Menu();
        menu.setVisible(true);
        setVisible(false);
    }
}
