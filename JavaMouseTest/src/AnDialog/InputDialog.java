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
 * An带文本输入的提示框
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
	 * 私有方法
	 */
	
	/**
	 * 初始化方法
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
		labTitle.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		labTitle.setBounds(10, 10, 292, 27);
		contentPane.add(labTitle);

		JLabel label_1 = new JLabel("\u8BF7\u8F93\u5165\uFF1A");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		label_1.setBounds(20, 47, 54, 15);
		contentPane.add(label_1);

		textField = new JTextField();
		textField.setBounds(68, 44, 213, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnOK = new JButton("确定");
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

		JButton btnCanncel = new JButton("取消");
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
		// 阻塞窗口
		setModal(true);
	}

	
	
	/*
	 * 
	 * 构造方法
	 */
	/**
	 * 
	 *	创建一个自定义窗口标题，内容标题，输入内容，输入文字判断的Dialog
	 * @param title_1 窗口标题
	 * @param title_2 内容标题
	 * @param content 输入框内容
	 * @param I 回调的确认函数
	 * @wbp.parser.constructor
	 */
	public InputDialog(String title_1, String title_2, String content,DialogContentVerify I) {
		initilize(null, title_1, title_2, content,I);
	}
	
	/**
	 * 创建一个自定义窗口标题，内容标题，输入文字判断的Dialog
	 * @param title_1 窗口标题
	 * @param title_2 内容标题
	 * @param I 回调的确认函数
	 */
	public InputDialog(String title_1, String title_2,DialogContentVerify I) {
		initilize(null, title_1, title_2, null,I);
	}
	
	/**
	 * 创建一个自定义窗口标题，内容标题的Dialog
	 * @param title_1 窗口标题
	 * @param title_2 内容标题
	 */
	public InputDialog(String title_1,String title_2){
		initilize(null, title_1, title_2, null, null);
	}
	
	
	
	
	/*
	 * 
	 * 公有方法
	 */
	
	/**
	 * 显示Dialog
	 * @return 返回按下的按键
	 */
	public DialogButton setVisible() {
		setVisible(true);
		return state;
	}
	/**
	 * 得到输入的文字
	 * @return 当前输入的文字
	 */
	public String getInputed(){
		return textField.getText();
	}
	
	
}
