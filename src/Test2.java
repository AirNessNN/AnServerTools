import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;

import BaseLanServices.BaseLanClient;
import BaseLanServices.ClientConnectionStateCallback;
import BaseLanServices.ServiceDevice;
import BaseLanServices.ServiceException;
import BaseLanServices.ServiceStateListener;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Test2 extends JFrame{
	private JTextField textField;
	
	private BaseLanClient client=null;
	JLabel state = new JLabel("未连接服务器");
	ObjectOutputStream oos;
	String name;
	boolean taskFlag;
	
	public Test2() {
		setTitle("\u5BA2\u6237\u7AEF");
		getContentPane().setLayout(null);
		//初始化客户端
		try {
			init();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 414, 204);
		getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnNewButton = new JButton("\u53D1\u9001");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					oos.writeObject(textField.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(331, 228, 93, 23);
		getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(10, 229, 311, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton button = new JButton("\u767B\u9646");
		button.setBounds(10, 260, 93, 23);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("\u4E0B\u7EBF");
		button_1.setBounds(113, 260, 93, 23);
		getContentPane().add(button_1);
		
		
		state.setBounds(216, 264, 208, 15);
		getContentPane().add(state);
		// TODO Auto-generated constructor stub
	}
	
	private void init() throws IOException {
		//实例化客户端服务
		client=new BaseLanClient("测试客户端", new ClientConnectionStateCallback() {
			
			@Override
			public void onClientDisconnect(Socket client) {
				// TODO Auto-generated method stub
				taskFlag=false;
				try {
					Test2.this.client.closeSocket();
				} catch ( IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state.setText("与服务器断开连接");
			}
			
			@Override
			public void onClientConnected(Socket client, InputStream in, OutputStream out) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							ObjectInputStream ois=new ObjectInputStream(in);
							oos=new ObjectOutputStream(out);
							taskFlag=true;
							while(taskFlag){
								int count=0;
								while(count==0&&taskFlag){
									count=ois.available();
								}
								String tmp=(String)ois.readObject();
								textField.setText(textField.getText()+"\n来自"+name+"的消息："+tmp);
							}
						} catch (IOException | ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		}, new ServiceStateListener() {
			
			@Override
			public boolean onServiceMsgReceived(ServiceDevice device) {
				// TODO Auto-generated method stub
				
				return true;
			}
			
			@Override
			public void onServiceDeviceStateChanged(int listSize, ServiceDevice device, byte state) {
				// TODO Auto-generated method stub
				name=new String(device.getCode());
				textField.setText("发现服务器："+name);
			}
		});
		client.setSkipLocalHost(false);
	}
	
	
}
