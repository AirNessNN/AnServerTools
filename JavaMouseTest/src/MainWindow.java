import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import BaseLanServices.*;
import MouseToolPkg.MouseMoveThreadListener;
import MouseToolPkg.MouseNode;
import MouseToolPkg.MouseOperation;
import MouseToolPkg.MouseTool;
import java.awt.Color;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;

public class MainWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel YLab ;
	private JLabel XLab;
	private JButton btnTranscribe;
	private JButton btnPlay;
	JButton btnNewButton;
	JButton btnNewButton_1;
	private JLabel stepLab;
	private JProgressBar progressBar;
	private JList list;
	private JCheckBox checkBox;
	private DefaultListModel<ServiceDevice> listModel=new DefaultListModel<ServiceDevice>();
	boolean flag=false;
	
	private MouseTool mouseTool;
	
	private BaseLanClient client=null;
	private BaseLanClientServer service=null;
	private JTextField textField;
	
	ObjectInputStream cois=null,sois=null;
	ObjectOutputStream coos,soos;
	
	
	public MainWindow() {
		setResizable(false);
		setSize(500, 768);
		getContentPane().setLayout(null);
		setTitle("鼠标测试");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		mouseTool=new MouseTool();
		
		JLabel label = new JLabel("\u9F20\u6807\u5750\u6807");
		label.setBounds(10, 10, 54, 15);
		getContentPane().add(label);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(74, 10, 19, 15);
		getContentPane().add(lblX);
		
		XLab= new JLabel("");
		XLab.setBounds(103, 10, 54, 15);
		getContentPane().add(XLab);
		
		JLabel lblNewLabel_1 = new JLabel("Y:");
		lblNewLabel_1.setBounds(74, 35, 19, 15);
		getContentPane().add(lblNewLabel_1);
		
		YLab= new JLabel("");
		YLab.setBounds(103, 35, 54, 15);
		getContentPane().add(YLab);
		
		btnTranscribe = new JButton("\u5F55\u5236");
		btnTranscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTranscribe.setEnabled(false);
				btnPlay.setEnabled(true);
				mouseTool.transcrible(new MouseOperation() {
					
					@Override
					public void addOperation(long step, ArrayList<MouseNode> mouseTrajectory) {
						// TODO Auto-generated method stub
						stepLab.setText(String.valueOf(step));
					}
				});
			}
		});
		btnTranscribe.setBounds(10, 60, 93, 23);
		getContentPane().add(btnTranscribe);
		
		btnPlay = new JButton("\u64AD\u653E");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTranscribe.setEnabled(true);
				progressBar.setMaximum((int)mouseTool.getStep());
				mouseTool.play(new MouseOperation() {
					
					@Override
					public void addOperation(long step, ArrayList<MouseNode> mouseTrajectory) {
						// TODO Auto-generated method stub
						progressBar.setValue((int)step);
					}
				});
			}
		});
		btnPlay.setBounds(113, 60, 93, 23);
		getContentPane().add(btnPlay);
		btnPlay.setEnabled(false);
		
		JLabel label_1 = new JLabel("\u6B65\u6570\uFF1A");
		label_1.setBounds(10, 96, 39, 18);
		getContentPane().add(label_1);
		
		stepLab = new JLabel("New label");
		stepLab.setBounds(61, 96, 55, 18);
		getContentPane().add(stepLab);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 163, 472, 14);
		getContentPane().add(progressBar);
		
		JLabel label_2 = new JLabel("\u64AD\u653E\u8FDB\u5EA6");
		label_2.setBounds(10, 133, 55, 18);
		getContentPane().add(label_2);
		
		checkBox = new JCheckBox("\u670D\u52A1\u5F00\u5173");
		
		checkBox.setBounds(10, 183, 103, 23);
		getContentPane().add(checkBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 212, 474, 112);
		getContentPane().add(scrollPane);
		
		list = new JList<ServiceDevice>(listModel);
		scrollPane.setViewportView(list);
		
		textField = new JTextField();
		textField.setBounds(10, 334, 216, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		btnNewButton = new JButton("\u53D1\u9001\u7ED9\u5BA2\u6237\u7AEF");
		btnNewButton.setBounds(236, 334, 131, 23);
		getContentPane().add(btnNewButton);
		
		btnNewButton_1= new JButton("\u53D1\u9001\u7ED9\u670D\u52A1\u7AEF");
		btnNewButton_1.setBounds(236, 367, 131, 23);
		getContentPane().add(btnNewButton_1);
		
		mouseTool.startMouseInfoThread(new MouseMoveThreadListener() {
			
			@Override
			public void mouseMovePerformed(int x, int y) {
				// TODO Auto-generated method stub
				XLab.setText(String.valueOf(x));
				YLab.setText(String.valueOf(y));
				if(flag){
					try {
						soos.writeObject(new Point(x,y));
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		});
		
		
		
		//初始化客户端和服务端
		try {
			client=new BaseLanClient("测试客户端", new ClientConnectionStateCallback() {
				
				@Override
				public void onClientDisconnect(Socket client) {
					// TODO Auto-generated method stub
					System.out.println("与服务器断开连接，服务器端口"+client.getPort()+" 服务器连接状态"+client.isConnected());
					try {
						cois.close();
						coos.close();
						cois=null;
						coos=null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void onClientConnected(Socket client,InputStream in,OutputStream out) {
					// TODO Auto-generated method stub
					System.out.println("连接上服务器");
					try {
						cois=new ObjectInputStream(in);
						coos=new ObjectOutputStream(out);
						
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								System.out.println("客户端开始接受数据");
								String tmp=null;
								do{
									Point p=(Point)cois.readObject();
									System.out.println(p.x+" "+p.y);
								}while(checkBox.isSelected());
								System.out.println("关闭客户端读取线程");
							} catch (ClassNotFoundException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}, new ServiceStateListener() {
				
				@Override
				public boolean onServiceMsgReceived(ServiceDevice sDevice) {
					// TODO Auto-generated method stub
					/*System.out.println(sDevice.getHost()+" "+new String(sDevice.getCode())+" "
					+((sDevice.getState()==ServiceState.SERVICE_IS_RUNNING)?"正在运行":"即将断开"));*/
					return true;
				}
				public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state) {
					// TODO Auto-generated method stub
					System.out.println("一个服务端被发现："+device.getHost()+"状态是"+device.getState());
					//测试TCP连接
					if(state==ServiceState.SERVICE_IS_RUNNING){
						listModel.addElement(device);
					}else{
						listModel.removeElement(device);
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		//定义测试服务器
		String name="测试服务器";
		byte[] b=name.getBytes();
		try {
			service=new BaseLanClientServer(4001,b,ConnectionModel.SingleConnection);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void initEvent() {
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					ServiceDevice device=(ServiceDevice) list.getSelectedValue();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								if(client.getConnectState()){
									client.disconnect();
								}else{
									
									//启动服务端
									try {
										/*mainWindow.service.stopUdpSendService();*/
										service.startService(occ);
									} catch (ServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Thread.sleep(20);
									client.creatNewConnect(4001);
									//客户端进行连接
									if(client.connect(device.getHost(),3000)){
										System.out.println("连接成功");
									}else{
										System.out.println("连接失败");
									}
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		
		checkBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(checkBox.isSelected()){
					service.startUdpSendService();
				}else{
					try {
						service.stopUdpSendService();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		
		
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					soos.writeObject(textField.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					try {
						soos.reset();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
		});
		//发送给服务端
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					coos.writeObject(textField.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	//服务端连接监听
	OnClientConnectListener occ=new OnClientConnectListener() {
		
		@Override
		public void onClientConnected(Socket client,InputStream in,OutputStream out,Message message) {
			// TODO Auto-generated method stub
			System.out.println("客户端已经连接，客户端电脑是"+message.computerName+" IP地址是"+message.host+"  服务器连接状态"+client.isClosed());
			try {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							sois=new ObjectInputStream(in);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				soos=new ObjectOutputStream(out);
				flag=true;
				System.out.println("服务端输入输出流初始化完成");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//服务端的数据服务
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						String tmp=null;
						System.out.println("服务器开始接受数据");
						do{
							tmp=(String)sois.readObject();
							System.out.println(tmp);
						}while(!tmp.equals("关闭"));
						
						service.closeService();
						service.clearConnectedClient();
						System.out.println("关闭服务端所有服务");
					} catch (ClassNotFoundException | IOException | ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	};

	
	
	
	
	
	public static void main(String[] args) {
		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainWindow mainWindow=new MainWindow();
		mainWindow.initEvent();
		
		mainWindow.setVisible(true);
		mainWindow.client.startBroadcase();
		
		
		
		
	}
}





