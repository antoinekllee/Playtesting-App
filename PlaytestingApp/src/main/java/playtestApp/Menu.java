package playtestApp;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Font;

public class Menu extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
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
	public Menu() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("PLAYTESTING APP");
		lblTitle.setBounds(190, 134, 378, 65);
		contentPane.add(lblTitle);
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 28));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(312, 269, 130, 28);
		contentPane.add(btnLogin);
		
		JButton btnSignup = new JButton("Signup");
		btnSignup.setBounds(312, 316, 130, 28);
		contentPane.add(btnSignup);
		
		ImageIcon icon = new ImageIcon(Menu.class.getResource("/images/background.jpg")); 
		Image image = icon.getImage(); 
    	Image newImg = image.getScaledInstance(800, 500, Image.SCALE_SMOOTH); 
    	icon = new ImageIcon(newImg); 
		JLabel lblImage = new JLabel("");
		lblImage.setBounds(0, 0, 800, 472);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setIcon(icon); 
		contentPane.add(lblImage);
		
		btnSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Signup signup = new Signup();
				signup.setVisible(true);

				// Close current window
				dispose();
			}
		});
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login login = new Login();
				login.setVisible(true);

				// Close current window
				dispose();
			}
		});

		setLocationRelativeTo(null);
		ColourManager.globalStyling(this); 
	}
}
