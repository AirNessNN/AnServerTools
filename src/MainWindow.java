import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import BaseLanServices.*;
import KeyboardTools.KeyboardHook;
import KeyboardTools.KeyboardListeners;
import MouseToolPkg.MouseThreadListener;
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

public class MainWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//鼠标Demo
	private JLabel YLab ;
	private JLabel XLab;
	private JButton btnTranscribe;
	private JButton btnPlay;
	private JLabel stepLab;
	private JProgressBar progressBar;
	private MouseTool mouseTool;//鼠标工具
	private KeyboardHook hook;//键盘工具
	
	//服务套件Demo
	private JList<ServiceDevice> list;
	private JCheckBox serverCheckBox;
	private DefaultListModel<ServiceDevice> listModel=new DefaultListModel<ServiceDevice>();
	boolean flag=false;//数据传送关闭标志
	private JCheckBox dataCheckBox;
	private BaseLanClient client=null;//客户端
	private BaseLanClientServer service=null;//服务端
	ObjectInputStream cois=null,sois=null;
	ObjectOutputStream coos,soos;
	
	
	Thread dataReadThread;
	
	JLabel recX;
	JLabel recY;
	
	//Demo构造
	public MainWindow() {
		setResizable(false);
		setSize(500, 768);
		getContentPane().setLayout(null);
		setTitle("测试Demo");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//定义测试服务器名字
		String name="测试服务器";
		byte[] b=name.getBytes();
		
		
		mouseTool=new MouseTool();
		//键盘监听
		hook=new KeyboardHook();
		
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
		btnTranscribe.setBounds(10, 60, 93, 23);
		getContentPane().add(btnTranscribe);
		
		btnPlay = new JButton("\u64AD\u653E");
		
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
		
		serverCheckBox = new JCheckBox("\u670D\u52A1\u5F00\u5173");
		
		serverCheckBox.setBounds(10, 183, 103, 23);
		getContentPane().add(serverCheckBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 212, 474, 112);
		getContentPane().add(scrollPane);
		
		list = new JList<ServiceDevice>(listModel);
		scrollPane.setViewportView(list);
		
		recX = new JLabel("X:");
		recX.setBounds(74, 334, 54, 15);
		getContentPane().add(recX);
		
		recY = new JLabel("Y:");
		recY.setBounds(74, 359, 54, 15);
		getContentPane().add(recY);
		
		JLabel label_5 = new JLabel("\u6536\u5230\u5750\u6807");
		label_5.setBounds(10, 334, 54, 15);
		getContentPane().add(label_5);
		
		dataCheckBox = new JCheckBox("\u6570\u636E\u4F20\u9001\u5F00\u5173");
		dataCheckBox.setBounds(180, 330, 103, 23);
		getContentPane().add(dataCheckBox);
		
		/*//初始化客户端和服务端
		try {
			client=new BaseLanClient("测试客户端", new ClientConnectionStateCallback() {
				
				@Override
				public void onClientDisconnect(Socket client) {
					// TODO Auto-generated method stub
					System.out.println("客户端：与服务器断开连接，服务器端口"+client.getPort()+" 服务器连接状态"+client.isConnected());
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
					System.out.println("客户端：连接上服务器");
					try {
						cois=new ObjectInputStream(in);
						coos=new ObjectOutputStream(out);
						//启动客户端数据接受线程
						dataReadThread=new Thread(new DataThreadRunnable());
						dataReadThread.start();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}, new ServiceStateListener() {
				
				@Override
				public boolean onServiceMsgReceived(ServiceDevice sDevice) {
					// TODO Auto-generated method stub
					System.out.println(sDevice.getHost()+" "+new String(sDevice.getCode())+" "
					+((sDevice.getState()==ServiceState.SERVICE_IS_RUNNING)?"已经运行":"即将断开"));
					return true;
				}
				public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state) {
					//测试TCP连接
					if(state==ServiceState.SERVICE_IS_RUNNING){
						listModel.addElement(device);
						System.out.println("客户端：一个服务端被发现："+device.getHost());
					}else{
						listModel.removeElement(device);
						System.out.println("客户端：一个服务端离线："+device.getHost());
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		//初始化服务端
		try {
			service=new BaseLanClientServer(4001,b,ConnectionModel.SingleConnection);
			service.startService(occ);
		} catch (IOException | ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}
	
	
	
	
	/**
	 * 初始化Demo控件事件
	 */
	public void initEvent() {
		//列表点击事件
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
									coos.writeObject("关闭");
									Thread.sleep(100);
									client.disconnect();
								}else{
									System.out.println("=============================================================");
									client.creatNewConnect(4001);
									//客户端进行连接
									if(client.connect(device.getHost(),3000)){
										System.out.println("客户端：连接成功");
									}else{
										System.out.println("客户端：连接失败");
									}
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
	
		//服务器服务开关点击事件
		serverCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(serverCheckBox.isSelected()){
					service.startUdpSendService();
				}else{
					try {
						service.stopUdpSendService();
						flag=false;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		//数据传输服务开关
		dataCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				flag=dataCheckBox.isSelected();
			}
		});
		
		//播放按钮
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTranscribe.setEnabled(true);
				btnPlay.setEnabled(false);
				progressBar.setMaximum((int)mouseTool.getStep());
				mouseTool.play(new MouseOperation() {
					
					@Override
					public void addOperation(int step, MouseNode[] mouseTrajectory) {
						// TODO Auto-generated method stub
						progressBar.setValue(step+1);
					}
				});
			}
		});
		
		//录制按钮
		btnTranscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTranscribe.setEnabled(false);
				btnPlay.setEnabled(true);
				mouseTool.transcrible(new MouseOperation() {
					
					@Override
					public void addOperation(int step, MouseNode[] mouseTrajectory) {
						// TODO Auto-generated method stub
						stepLab.setText(String.valueOf(step));
					}
				});
			}
		});
		
		//鼠标工具开始监听
		mouseTool.startMouseInfoThread(new MouseThreadListener() {
			
			@Override
			public void mouseMovePerformed(int x, int y) {
				// TODO Auto-generated method stub
				XLab.setText(String.valueOf(x));
				YLab.setText(String.valueOf(y));
				
				//传输鼠标坐标
				if(flag&&dataCheckBox.isSelected()){
					try {
						soos.writeObject(new Point(x,y));
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}

			@Override
			public void mouseActionPerformed(int action) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//键盘钩子事件
		hook.startKeyboardListening(new KeyboardListeners() {
			
			@Override
			public void actionPerformed(int vCode,boolean isDown) {
				// TODO Auto-generated method stub
				System.out.println(KeyEvent.getKeyText(vCode)+(isDown?"按下":"放开"));
			}
		});
		
		
		
		
	}
	
	
	
	
	//服务端连接监听
	/*OnClientConnectListener occ=new OnClientConnectListener() {
		
		@Override
		public void onClientConnected(Socket client,InputStream in,OutputStream out,Message message) {
			// TODO Auto-generated method stub
			System.out.println("服务端：["+message.name+"]已经连接，客户端电脑是"+message.computerName+" IP地址是"+message.host+"  服务器连接状态"+client.isClosed());
			try {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							sois=new ObjectInputStream(in);//因为InputStream会阻塞，所以要新开线程
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				soos=new ObjectOutputStream(out);
				//初始化完成，可以传输坐标
				flag=true;
				System.out.println("服务端：服务端输入输出流初始化完成");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//服务端的数据服务线程，用于监听客户端的数据流关闭
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						String tmp=null;
						System.out.println("服务端：开始接受数据");
						do{
							tmp=(String)sois.readObject();
							System.out.println("服务端：收到命令："+tmp);
						}while(!tmp.equals("关闭"));
						if(!flag){
							try {
								soos.writeObject(new Point(-1,-1));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						service.closeService();
						service.clearConnectedClient();
						System.out.println("服务端：关闭所有服务");
						service.startService(occ);
						System.out.println("服务端：服务重启");
					} catch (ClassNotFoundException | IOException | ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	};*/
	
	void playAuto() throws IOException, ClassNotFoundException {
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView(); 
		File file=new File(fsv.getDefaultDirectory().getAbsolutePath()+"\\as.b");
		if(file.exists()) {
			this.setVisible(false);
			FileInputStream in=new FileInputStream(file);
			ObjectInputStream ois=new ObjectInputStream(in);
			mouseTool.play((ArrayList<MouseNode>)ois.readObject());
			ois.close();
		}
	}

	//客户端接受服务端鼠标坐标的线程实现
	class DataThreadRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				System.out.println("客户端：开始接受数据");
				do{
					/*
					 * 如果断开连接，会导致read异常，没有关系，异常之后就会停止
					 * 并且可以正常连接
					 */
					Object obj=cois.readObject();
					Point p=null;
					if(obj instanceof Point){
						p=(Point)obj;
					}
					//System.out.println(p.x+" "+p.y);
					if(p.x==-1){
						break;
					}
					recX.setText(String.valueOf(p.getX()));
					recY.setText(String.valueOf(p.getY()));
				}while(true);
				System.out.println("客户端：数据读取线程已经结束");
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		
	}
	
	
	
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
		try {
			mainWindow.playAuto();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//mainWindow.client.startBroadcase();//客户端默认开启接受广播
		
		/*Robot robot=null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		while(true) {
			long startTime=System.currentTimeMillis();
			
			robot.createScreenCapture(new Rectangle(d));
			long endtime=System.currentTimeMillis();
			System.out.println("FPS:"+1000/(endtime-startTime)+"  "+(endtime-startTime));
		}*/
		
		
		
		
	}
}





