package BaseLanServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
/**
 * BaseLAN���࣬�����ľ����������������ʵ�־������ڿ���ͨ�źͶ�ͻ�������<br/>
 * <br>
 *����ʵ�ֵ�һ����Ҳ����ʵ��һ�Զ���߳����ӣ���Ҫע����ǣ����׼�ֻʵ�����ӹ��ܣ������ṩ���ݴ���ı�׼��Ҳ����˵
 *���������ݺʹ���������Ҫ��ȡ���ڲ����������������������ʹ�÷����뿴���������ṩ��ʾ����
 * <br>
 * 
 * <br>�˷����ش���������{@code Socket}    {@code Message}
 * <br>
 * <br>
 * �����еķ����У�
 * <li>
 * 	<ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * <ul>{@code boolean getConnectState()}</ul>
 * </li>
 * @author AN
 * 
 *
 */
public class BaseLanService {

	
	
	//����
	/**
	 * �Ƿ�������״̬
	 */
	private boolean isConnected;
	/**
	 * ��������״̬������true��һ����һ�����ϵ�����
	 */
	public boolean getConnectState() {
		return isConnected;
	}
	private boolean isUdpServiceStart;
	/**
	 * ����UDP�����Ƿ��
	 * @return
	 */
	public boolean getUdpServiceState() {
		return isUdpServiceStart;
	}
	
	
	
	//�ͻ���ʵ��
	private Socket clientSocket=null;
	private ArrayList<Socket>clientSocketList=null;
	private ServerSocket serverSocket=null;
	public boolean isServiceRunning;
	
	private UdpSender sender;
	
	//���������
	private InputStream in=null;
	private OutputStream out=null;
	
	//������Ϣ
	private Message clientMsg=null;
	private ArrayList<Message> clientMsgList=null;
	
	private int port;
	
	
	
	
	
	
	//���ݳ�ʼ��
	private void initilize(int port,byte[] data) throws IOException{
		serverSocket=new ServerSocket(port);
		isConnected=false;
		isServiceRunning=false;
		this.port=port;
		clientSocketList=new ArrayList<Socket>();
	}
	
	
	
	
	
	

	/**
	 * ����ServerSocket����
	 * @return 
	 */
	public ServerSocket getServerSocket(){return serverSocket;}
	
	
	
	
	
	/**
	 * ����һ���󶨶˿ڵķ���ServerSocket
	 * @param port ��Ҫ�󶨵Ķ˿�
	 * @throws IOException ʵ����ʧ�ܻ��׳��쳣
	 */
	public BaseLanService(int port) throws IOException {
		//��ʼ������
		initilize(port,null);
		sender=new UdpSender(null);
	}
	/**
	 * ����һ���Զ���UPD�㲥ʶ����ķ���ServiceSocket
	 * @param port ��Ҫ�󶨵Ķ˿�
	 * @param data �Զ����ʶ����
	 */
	public BaseLanService(int port,byte[] data) throws IOException {
		// TODO Auto-generated constructor stub
		try {
			initilize(port, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender=new UdpSender(data);
	}
	
	
	
	
	
	
	
	/**
	 * ���ص�ǰ�����ŵ�Socket����,�����Ϻ��Ե�һ����ģʽ����һ���ӿ��Ե���getSingleConnectState����
	 * @return int count
	 */
	public int getConnectedCount() {
		int count=0;
		if(clientSocketList.size()>0){
			for(Socket socket : clientSocketList){
				if(socket.isBound()){
					count++;
				}
			}
		}
		return count;
	}
	
	
	
	
	
	
	/**
	 * ���ص�ǰ��һ���ӷ�ʽ�Ŀͻ��˵�����״̬
	 * @return true��������Զ�̿ͻ��ˣ�falseδ����
	 */
	public boolean getSingleConnectState() {
		return clientSocket.isBound();
	}
	
	
	
	
	
	
	/**
	 * ��UPD�㲥
	 * @return
	 */
	public boolean startUdpSendService() {
		if(sender!=null&&!sender.isTaskRunning){
			sender.startService();
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * �ر�Udp�㲥
	 * @throws IOException 
	 */
	public boolean stopUdpSendService() throws IOException {
		if(sender!=null&&sender.isTaskRunning){
			sender.stopService();
			return true;
		}
		return false;
	}
	
	
	
	
	/*
	 * 
	 * 
	 * ���񿪹�ϵ��
	 */
	
	
	/**
	 * ������һ�ͻ��˼���������Ҫʵ�ּ����ķ���
	 * @param I �ص��ӿ�
	 * @return �����ɹ�����true
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public boolean startSingleService(OnClientConnectListener I) throws ServiceException, IOException {
		if(isServiceRunning||isConnected){
			throw new ServiceException("�����������л���������Զ�̿ͻ���");
		}
		if(serverSocket==null){
			serverSocket=new ServerSocket(port);
		}
		isServiceRunning=true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					clientSocket=serverSocket.accept();
					isConnected=true;
					//��ȡ��һ��������Ϣ
					in=clientSocket.getInputStream();
					out=clientSocket.getOutputStream();
					//Debug+++++++++++++
					clientSocket.isClosed();
					//+++++++++++++++++
					int count = 0;
					while (count == 0) {
						count = in.available();
					}
					byte[] b = new byte[count];
					int readCount = 0; // �Ѿ��ɹ���ȡ���ֽڵĸ���
					while (readCount < count) {
						readCount += in.read(b, readCount, count - readCount);
					}
					System.out.println(b.length);
					clientMsg=(Message)Utils.toObject(b);
					
					//���ýӿ�
					I.onClientConnected(clientSocket,in,out,clientMsg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					serverSocket=null;
					clientSocket=null;
					clientMsg=null;
					in=null;
					out=null;
					isConnected=false;
					isServiceRunning=false;
				}
			}
		}).start();
		return true;
	}
	
	
	
	
	
	
	
	
	/**
	 * ������ͻ��˼���������Ҫʵ�ּ����ķ���
	 * @return
	 */
	public boolean startMultipleServiceMonitor(OnClientConnectListener I){
		if(serverSocket==null){//��ʼ���ж�
			return false;
		}
		if(clientSocketList==null){
			clientSocketList=new ArrayList<Socket>();
		}
		isServiceRunning=true;//�򿪿���
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isServiceRunning){
					try {
						Socket client=serverSocket.accept();//���ܿͻ��˵�����
						isConnected=true;
						//��ȡ��һ��������Ϣ
						InputStream in=client.getInputStream();
						//Debug+++++++++++++
						client.isClosed();
						//+++++++++++++++++
						int count = 0;
						while (count == 0) {
							count = in.available();
						}
						byte[] b = new byte[count];
						int readCount = 0; // �Ѿ��ɹ���ȡ���ֽڵĸ���
						while (readCount < count) {
							readCount += in.read(b, readCount, count - readCount);
						}
						Message clientMsg=(Message)Utils.toObject(b);
						
						//���ýӿ�
						I.onClientConnected(client,client.getInputStream(),client.getOutputStream(),clientMsg);
					} catch (IOException e) {
						e.printStackTrace();
						isServiceRunning=false;//ֹͣ���ܷ���
					}
				}
			}
		}).start();
		return true;
	}
	
	
	
	
	
	
	
	/**
	 * �ر�Service�����̷߳���
	 */
	public void closeService(){
		isServiceRunning=false;
	}
	
	
	
	
	
	
	/**
	 * ����Ѿ����ӵĿͻ���
	 * @throws ServiceException �ڷ�������ʱ��ջ��׳��쳣
	 * @throws IOException ���ر�ʱ����IO����
	 */
	public void clearConnectedClient() throws ServiceException, IOException{
		if(isServiceRunning){
			throw new ServiceException("Service����û��ֹͣ");
		}
		if(isConnected){
			if(clientMsgList!=null){
				for(Socket socket :clientSocketList){
					if(socket.isBound()){
						socket.close();
					}
				}
				if(clientSocketList.size()>0){
					clientSocketList.clear();
				}
				clientSocketList=null;
			}
		}
		if(clientSocket!=null){
			if(in!=null){
				in.close();
				in=null;
			}
			if(out!=null){
				out.close();
				out=null;
			}
			clientSocket.close();
			clientSocket=null;
		}
		isConnected=false;
	}

	
	
	
	
	
	
	/**
	 * ����һ���������еĵ�һģʽ��Socket
	 * @return ����null���Socketδ���ӻ��߿�
	 */
	public Socket getConnectedSocket() {
		if(clientSocket!=null&&clientSocket.isConnected()){
			return clientSocket;
		}
		return null;
	}
	
	
	public InputStream getInputStream() {
		if(isConnected){
			if(in==null){
				return null;
			}
			return in;
		}
		return null;
	}
	
	public OutputStream getOutputStream() {
		if(isConnected){
			if(out==null){
				return null;
			}
			return out;
		}
		return null;
	}
}







//UDP������������
class UdpSender implements Runnable{
	//����
	private static final int PORT=4001;//�˿�
	private static final String HOST = "224.255.10.0";//�㲥��
	private static final int IP_BYTE_LENGTH=14;
	
	//��Ա����
	public long  timeValue=1000;
	
	//�����߳�
	private Thread task;
	
	//�㲥�߳�
	public boolean isTaskRunning;
	
	
	//���㲥�׽���
	private MulticastSocket socket = null;
	private InetAddress iaddress = null;
	
	//����IP��ַ��IP��ַ���ֽ���
	public String IPconfig;
	private byte[] data;
	private int dataLen;
	
	
	//��ʼ���¼�
	private void initilizeEvent() {
		task=new Thread(this);
	}
	
	//��ʼ������
	private void initilizeData(byte[] data) {
		try {
			IPconfig = InetAddress.getLocalHost().getHostAddress();
			iaddress = InetAddress.getByName(HOST);
			socket = new MulticastSocket(PORT);// ��ʼ�����㲥
			socket.setTimeToLive(1);// ���ع㲥
			socket.joinGroup(iaddress);// ������
			ArrayList<Byte> tmpData=new ArrayList<Byte>();
			//�����û�ʶ����
			if(data!=null){
				for(byte b: data){
					tmpData.add(new Byte(b));
				}
			}
			//����״̬
			tmpData.add(new Byte((byte) 0));
			//�����ַ
			byte[] tmpIP=IPconfig.getBytes();
			for(byte b:tmpIP){
				tmpData.add(new Byte(b));
			}
			//ת��������
			dataLen=tmpData.size();
			byte[] d=new byte[dataLen];
			int index=0;
			for(Byte b : tmpData){
				d[index++]=b.byteValue();
			}
			this.data=d;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			socket=null;
			data=null;
			dataLen=0;
			task=null;
			timeValue=1000;
		}
	}
	
	//����
	public UdpSender(byte[] data) {
		
		initilizeEvent();
		
		initilizeData(data);
	}
	
	//�����㲥
	public void startService() {
		if(!isTaskRunning){
			if(task!=null){
				task=new Thread(this);
			}
			task.start();
		}
	}
	//�رչ㲥
	public void stopService() throws IOException {
		if(isTaskRunning){
			//�ر�����
			isTaskRunning=false;
			//�㲥һ���رշ������Ϣ
			byte[] tmp=null;
			tmp=Arrays.copyOf(data, dataLen);
			tmp[dataLen-14]=(byte)1;
			DatagramPacket packet = null;
			packet = new DatagramPacket(tmp, tmp.length, iaddress, PORT);
			socket.send(packet);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		isTaskRunning=true;
		while(isTaskRunning){
			DatagramPacket packet = null;
			packet = new DatagramPacket(data, data.length, iaddress, PORT);
			try {
				socket.send(packet);
				Thread.sleep(timeValue);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
