import java.awt.*;

import javax.swing.*;

import BaseLanServices.Utils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Window extends JFrame{
	
	private Container c=getContentPane();
	
	private JButton jb1;
	private JTextField textField;
	JLabel result;
	
	public Window() {
		// TODO Auto-generated constructor stub
		setTitle("´°¿Ú");
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 10, 193, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result.setText(Utils.isRightHostAddress(textField.getText())?"ÕýÈ·":"´íÎó");
			}
		});
		btnNewButton.setBounds(213, 9, 93, 23);
		getContentPane().add(btnNewButton);
		
		result = new JLabel("New label");
		result.setBounds(10, 41, 54, 15);
		getContentPane().add(result);
		
		
	}
}
