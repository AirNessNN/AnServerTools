package AnDialog;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * An���ı��������ʾ��
 * @author AN
 *
 */
public class InputDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JLabel labTitle;
	private DialogButton state = null;

	
	/*
	 * 
	 * ˽�з���
	 */
	
	/**
	 * ��ʼ������
	 * @param parent
	 * @param windowsTitle
	 * @param title
	 * @param content
	 * @param I
	 */
	private void initilize(JFrame parent, String windowsTitle, String title, String content,DialogContentVerify I) {
		setBounds(100, 100, 334, 178);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		labTitle = new JLabel("\u6807\u9898");
		labTitle.setFont(new Font("΢���ź�", Font.PLAIN, 16));
		labTitle.setBounds(10, 10, 292, 27);
		contentPane.add(labTitle);

		JLabel label_1 = new JLabel("\u8BF7\u8F93\u5165\uFF1A");
		label_1.setFont(new Font("΢���ź�", Font.PLAIN, 12));
		label_1.setBounds(20, 47, 54, 15);
		contentPane.add(label_1);

		textField = new JTextField();
		textField.setBounds(68, 44, 213, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnOK = new JButton("ȷ��");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(I!=null){
					if(I.confirm(textField.getText())){
						state=DialogButton.OK_BUTTON;
						setVisible(false);
					}else{
						return;
					}
				}
			}
		});
		btnOK.setBounds(105, 99, 93, 23);
		contentPane.add(btnOK);

		JButton btnCanncel = new JButton("ȡ��");
		btnCanncel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state = DialogButton.CANNCEL_BUTTON;
				textField.setText(null);
				InputDialog.this.dispose();
			}
		});
		btnCanncel.setBounds(209, 99, 93, 23);
		contentPane.add(btnCanncel);
		

		this.setTitle(windowsTitle);
		this.labTitle.setText(title);
		this.textField.setText(content);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		// ��������
		setModal(true);
	}

	
	
	/*
	 * 
	 * ���췽��
	 */
	/**
	 * 
	 *	����һ���Զ��崰�ڱ��⣬���ݱ��⣬�������ݣ����������жϵ�Dialog
	 * @param title_1 ���ڱ���
	 * @param title_2 ���ݱ���
	 * @param content ���������
	 * @param I �ص���ȷ�Ϻ���
	 * @wbp.parser.constructor
	 */
	public InputDialog(String title_1, String title_2, String content,DialogContentVerify I) {
		initilize(null, title_1, title_2, content,I);
	}
	
	/**
	 * ����һ���Զ��崰�ڱ��⣬���ݱ��⣬���������жϵ�Dialog
	 * @param title_1 ���ڱ���
	 * @param title_2 ���ݱ���
	 * @param I �ص���ȷ�Ϻ���
	 */
	public InputDialog(String title_1, String title_2,DialogContentVerify I) {
		initilize(null, title_1, title_2, null,I);
	}
	
	/**
	 * ����һ���Զ��崰�ڱ��⣬���ݱ����Dialog
	 * @param title_1 ���ڱ���
	 * @param title_2 ���ݱ���
	 */
	public InputDialog(String title_1,String title_2){
		initilize(null, title_1, title_2, null, null);
	}
	
	
	
	
	/*
	 * 
	 * ���з���
	 */
	
	/**
	 * ��ʾDialog
	 * @return ���ذ��µİ���
	 */
	public DialogButton setVisible() {
		setVisible(true);
		return state;
	}
	/**
	 * �õ����������
	 * @return ��ǰ���������
	 */
	public String getInputed(){
		return textField.getText();
	}
	
	
}
