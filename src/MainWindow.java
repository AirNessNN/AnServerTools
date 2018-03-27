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
	//���Demo
	private JLabel YLab ;
	private JLabel XLab;
	private JButton btnTranscribe;
	private JButton btnPlay;
	private JLabel stepLab;
	private JProgressBar progressBar;
	private MouseTool mouseTool;//��깤��
	private KeyboardHook hook;//���̹���
	
	//�����׼�Demo
	private JList<ServiceDevice> list;
	private JCheckBox serverCheckBox;
	private DefaultListModel<ServiceDevice> listModel=new DefaultListModel<ServiceDevice>();
	boolean flag=false;//���ݴ��͹رձ�־
	private JCheckBox dataCheckBox;
	private BaseLanClient client=null;//�ͻ���
	private BaseLanClientServer service=null;//�����
	ObjectInputStream cois=null,sois=null;
	ObjectOutputStream coos,soos;
	
	
	Thread dataReadThread;
	
	JLabel recX;
	JLabel recY;
	
	//Demo����
	public MainWindow() {
		setResizable(false);
		setSize(500, 768);
		getContentPane().setLayout(null);
		setTitle("����Demo");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//������Է���������
		String name="���Է�����";
		byte[] b=name.getBytes();
		
		
		mouseTool=new MouseTool();
		//���̼���
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
		
		/*//��ʼ���ͻ��˺ͷ����
		try {
			client=new BaseLanClient("���Կͻ���", new ClientConnectionStateCallback() {
				
				@Override
				public void onClientDisconnect(Socket client) {
					// TODO Auto-generated method stub
					System.out.println("�ͻ��ˣ���������Ͽ����ӣ��������˿�"+client.getPort()+" ����������״̬"+client.isConnected());
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
					System.out.println("�ͻ��ˣ������Ϸ�����");
					try {
						cois=new ObjectInputStream(in);
						coos=new ObjectOutputStream(out);
						//�����ͻ������ݽ����߳�
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
					+((sDevice.getState()==ServiceState.SERVICE_IS_RUNNING)?"�Ѿ�����":"�����Ͽ�"));
					return true;
				}
				public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state) {
					//����TCP����
					if(state==ServiceState.SERVICE_IS_RUNNING){
						listModel.addElement(device);
						System.out.println("�ͻ��ˣ�һ������˱����֣�"+device.getHost());
					}else{
						listModel.removeElement(device);
						System.out.println("�ͻ��ˣ�һ����������ߣ�"+device.getHost());
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		//��ʼ�������
		try {
			service=new BaseLanClientServer(4001,b,ConnectionModel.SingleConnection);
			service.startService(occ);
		} catch (IOException | ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}
	
	
	
	
	/**
	 * ��ʼ��Demo�ؼ��¼�
	 */
	public void initEvent() {
		//�б����¼�
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
									coos.writeObject("�ر�");
									Thread.sleep(100);
									client.disconnect();
								}else{
									System.out.println("=============================================================");
									client.creatNewConnect(4001);
									//�ͻ��˽�������
									if(client.connect(device.getHost(),3000)){
										System.out.println("�ͻ��ˣ����ӳɹ�");
									}else{
										System.out.println("�ͻ��ˣ�����ʧ��");
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
	
		//���������񿪹ص���¼�
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
		
		//���ݴ�����񿪹�
		dataCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				flag=dataCheckBox.isSelected();
			}
		});
		
		//���Ű�ť
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
		
		//¼�ư�ť
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
		
		//��깤�߿�ʼ����
		mouseTool.startMouseInfoThread(new MouseThreadListener() {
			
			@Override
			public void mouseMovePerformed(int x, int y) {
				// TODO Auto-generated method stub
				XLab.setText(String.valueOf(x));
				YLab.setText(String.valueOf(y));
				
				//�����������
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
		
		//���̹����¼�
		hook.startKeyboardListening(new KeyboardListeners() {
			
			@Override
			public void actionPerformed(int vCode,boolean isDown) {
				// TODO Auto-generated method stub
				System.out.println(KeyEvent.getKeyText(vCode)+(isDown?"����":"�ſ�"));
			}
		});
		
		
		
		
	}
	
	
	
	
	//��������Ӽ���
	/*OnClientConnectListener occ=new OnClientConnectListener() {
		
		@Override
		public void onClientConnected(Socket client,InputStream in,OutputStream out,Message message) {
			// TODO Auto-generated method stub
			System.out.println("����ˣ�["+message.name+"]�Ѿ����ӣ��ͻ��˵�����"+message.computerName+" IP��ַ��"+message.host+"  ����������״̬"+client.isClosed());
			try {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							sois=new ObjectInputStream(in);//��ΪInputStream������������Ҫ�¿��߳�
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				soos=new ObjectOutputStream(out);
				//��ʼ����ɣ����Դ�������
				flag=true;
				System.out.println("����ˣ�����������������ʼ�����");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//����˵����ݷ����̣߳����ڼ����ͻ��˵��������ر�
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						String tmp=null;
						System.out.println("����ˣ���ʼ��������");
						do{
							tmp=(String)sois.readObject();
							System.out.println("����ˣ��յ����"+tmp);
						}while(!tmp.equals("�ر�"));
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
						System.out.println("����ˣ��ر����з���");
						service.startService(occ);
						System.out.println("����ˣ���������");
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

	//�ͻ��˽��ܷ�������������߳�ʵ��
	class DataThreadRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				System.out.println("�ͻ��ˣ���ʼ��������");
				do{
					/*
					 * ����Ͽ����ӣ��ᵼ��read�쳣��û�й�ϵ���쳣֮��ͻ�ֹͣ
					 * ���ҿ�����������
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
				System.out.println("�ͻ��ˣ����ݶ�ȡ�߳��Ѿ�����");
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
		
		
		//mainWindow.client.startBroadcase();//�ͻ���Ĭ�Ͽ������ܹ㲥
		
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





