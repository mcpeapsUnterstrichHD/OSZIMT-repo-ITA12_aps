import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.net.URI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class CipherWindow {
private JFrame frmOszimtcrypter;
private File encryptedFile;
private JEditorPane editorPane;
private String password;
private String username;
private UserManagement um = new UserManagement();
private SecureEnclave se = new SecureEnclave();
private FileHandler fh = new FileHandler();




    public CipherWindow(String password, String username) {
    	this.setPassword(password);
    	this.setUsername(username);
        initialize();
    }

    private void initialize() {
        frmOszimtcrypter = new JFrame();
        frmOszimtcrypter.setTitle("OSZimt-Crypter");
        frmOszimtcrypter.setBounds(100, 100, 450, 300);
        frmOszimtcrypter.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frmOszimtcrypter.setMinimumSize(new Dimension(450, 300));
        frmOszimtcrypter.addWindowListener(new WindowAdapter() {
        	   public void windowClosing(WindowEvent evt) {
        		     onExit();
        		   }
        		  });
        

        JMenuBar menuBar = new JMenuBar();
        frmOszimtcrypter.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem openItem = new JMenuItem("Open File");
        fileMenu.add(openItem);
        
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                   encryptedFile = fileChooser.getSelectedFile();
                    // Rename the file
                    // ...
                }
            }
        });
        
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        fileMenu.add(saveAsMenuItem);
        
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                	encryptedFile = fileChooser.getSelectedFile();
                }
            }
        });
        
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        JMenuItem helpLinkItem = new JMenuItem("Help-Wiki");
        helpMenu.add(helpLinkItem);
        
        helpLinkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
                    URI uri = new URI("http://ita12docoszimt.serveblog.net");
                    Desktop.getDesktop().browse(uri);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        JMenuItem logOutFromUser = new JMenuItem("Log out from: " + this.getUsername());
        helpMenu.add(logOutFromUser);
        
        logOutFromUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				um.removeUser(getUsername());
				onLogOut();
				
			}
		});
        
        JMenuItem deleteUser = new JMenuItem("Log out and delete: " + this.getUsername());
        helpMenu.add(deleteUser);
        
        deleteUser.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
        		
        		onLogOut();
        		um.removeUser(getUsername());
        	}
        });

        JPanel pnl1 = new JPanel();
        pnl1.setLayout(new BorderLayout(0, 0));
        frmOszimtcrypter.getContentPane().add(pnl1);

        JPanel pnlbutton = new JPanel();
        pnl1.add(pnlbutton, BorderLayout.SOUTH);

        JButton btnNewButton = new JButton("Encrypt");
        btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
                String plaintext = editorPane.getText();
                byte[] encryptedData = se.encrypt(password, plaintext);
                try {
					fh.writeFile(encryptedFile, encryptedData);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                editorPane.setText("");
            }
        });
       

        JButton btnNewButton_1 = new JButton("Decrypt");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                                
                byte[] encryptedData;
				try {
					encryptedData = fh.readFile(encryptedFile);
	                String decryptedText = se.decrypt(password, encryptedData);
	                editorPane.setText(decryptedText);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

            }
        });
        

        editorPane = new JEditorPane();
        pnl1.add(editorPane, BorderLayout.CENTER);
        pnlbutton.add(btnNewButton);
        pnlbutton.add(btnNewButton_1);

        encryptedFile = new File(getUsername()+"_data.oszcryped");

        frmOszimtcrypter.setVisible(true);
    }



    public static void main(String[] args) {
        new CipherWindow("fabian66", "mahd");
    }

    public void onExit() {
    	String plaintext = editorPane.getText();
        byte[] encryptedData = se.encrypt(password, plaintext);
        try {
			fh.writeFile(encryptedFile, encryptedData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        System.exit(0);
    }
    
    public void onLogOut() {
    	String plaintext = editorPane.getText();
        byte[] encryptedData = se.encrypt(password, plaintext);
        try {
			fh.writeFile(encryptedFile, encryptedData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        frmOszimtcrypter.dispose();
        new LoginWindow();
    }

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}