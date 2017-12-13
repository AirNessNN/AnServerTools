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
 * BaseLAN基类，基本的局域网服务器组件，实现局域网内快速通信和多客户端连接<br/>
 * <br>
 *可以实现单一连接也可以实现一对多多线程连接，需要注意的是，此套件只实现连接功能，并不提供数据传输的标准，也就是说
 *，传输数据和传输内容需要获取到内部的输入流和输出流，具体使用方法请看各个方法提供的示例。
 * <br>
 * 
 * <br>此方法回传两个参数{@code Socket}    {@code Message}
 * <br>
 * <br>
 * 此类中的方法有：
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

	
	
	//属性
	/**
	 * 是否是连接状态
	 */
	private boolean isConnected;
	/**
	 * 返回连接状态，返回true有一个或一个以上的连接
	 */
	public boolean getConnectState() {
		return isConnected;
	}
	private boolean isUdpServiceStart;
	/**
	 * 返回UDP服务是否打开
	 * @return
	 */
	public boolean getUdpServiceState() {
		return isUdpServiceStart;
	}
	
	
	
	//客户端实例
	private Socket clientSocket=null;
	private ArrayList<Socket>clientSocketList=null;
	private ServerSocket serverSocket=null;
	public boolean isServiceRunning;
	
	private UdpSender sender;
	
	//输入输出流
	private InputStream in=null;
	private OutputStream out=null;
	
	//配置信息
	private Message clientMsg=null;
	private ArrayList<Message> clientMsgList=null;
	
	private int port;
	
	
	
	
	
	
	//数据初始化
	private void initilize(int port,byte[] data) throws IOException{
		serverSocket=new ServerSocket(port);
		isConnected=false;
		isServiceRunning=false;
		this.port=port;
		clientSocketList=new ArrayList<Socket>();
	}
	
	
	
	
	
	

	/**
	 * 返回ServerSocket对象
	 * @return 
	 */
	public ServerSocket getServerSocket(){return serverSocket;}
	
	
	
	
	
	/**
	 * 构造一个绑定端口的服务ServerSocket
	 * @param port 需要绑定的端口
	 * @throws IOException 实例化失败会抛出异常
	 */
	public BaseLanService(int port) throws IOException {
		//初始化数据
		initilize(port,null);
		sender=new UdpSender(null);
	}
	/**
	 * 构造一个自定义UPD广播识别码的服务ServiceSocket
	 * @param port 需要绑定的端口
	 * @param data 自定义的识别码
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
	 * 返回当前连接着的Socket数量,数量上忽略单一连接模式，单一连接可以调用getSingleConnectState（）
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
	 * 返回当前单一连接方式的客户端的连接状态
	 * @return true已连接上远程客户端，false未连接
	 */
	public boolean getSingleConnectState() {
		return clientSocket.isBound();
	}
	
	
	
	
	
	
	/**
	 * 打开UPD广播
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
	 * 关闭Udp广播
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
	 * 服务开关系列
	 */
	
	
	/**
	 * 开启单一客户端监听服务，需要实现监听的方法
	 * @param I 回调接口
	 * @return 启动成功返回true
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public boolean startSingleService(OnClientConnectListener I) throws ServiceException, IOException {
		if(isServiceRunning||isConnected){
			throw new ServiceException("服务正在运行或已连接上远程客户端");
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
					//获取第一个配置信息
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
					int readCount = 0; // 已经成功读取的字节的个数
					while (readCount < count) {
						readCount += in.read(b, readCount, count - readCount);
					}
					System.out.println(b.length);
					clientMsg=(Message)Utils.toObject(b);
					
					//调用接口
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
	 * 开启多客户端监听服务，需要实现监听的方法
	 * @return
	 */
	public boolean startMultipleServiceMonitor(OnClientConnectListener I){
		if(serverSocket==null){//初始化判断
			return false;
		}
		if(clientSocketList==null){
			clientSocketList=new ArrayList<Socket>();
		}
		isServiceRunning=true;//打开开关
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isServiceRunning){
					try {
						Socket client=serverSocket.accept();//接受客户端的连接
						isConnected=true;
						//获取第一个配置信息
						InputStream in=client.getInputStream();
						//Debug+++++++++++++
						client.isClosed();
						//+++++++++++++++++
						int count = 0;
						while (count == 0) {
							count = in.available();
						}
						byte[] b = new byte[count];
						int readCount = 0; // 已经成功读取的字节的个数
						while (readCount < count) {
							readCount += in.read(b, readCount, count - readCount);
						}
						Message clientMsg=(Message)Utils.toObject(b);
						
						//调用接口
						I.onClientConnected(client,client.getInputStream(),client.getOutputStream(),clientMsg);
					} catch (IOException e) {
						e.printStackTrace();
						isServiceRunning=false;//停止接受服务
					}
				}
			}
		}).start();
		return true;
	}
	
	
	
	
	
	
	
	/**
	 * 关闭Service监听线程服务
	 */
	public void closeService(){
		isServiceRunning=false;
	}
	
	
	
	
	
	
	/**
	 * 清空已经连接的客户端
	 * @throws ServiceException 在服务运行时清空会抛出异常
	 * @throws IOException 当关闭时出现IO错误
	 */
	public void clearConnectedClient() throws ServiceException, IOException{
		if(isServiceRunning){
			throw new ServiceException("Service服务没有停止");
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
	 * 返回一个在连接中的单一模式的Socket
	 * @return 返回null如果Socket未连接或者空
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







//UDP局域网发送器
class UdpSender implements Runnable{
	//常量
	private static final int PORT=4001;//端口
	private static final String HOST = "224.255.10.0";//广播组
	private static final int IP_BYTE_LENGTH=14;
	
	//成员变量
	public long  timeValue=1000;
	
	//任务线程
	private Thread task;
	
	//广播线程
	public boolean isTaskRunning;
	
	
	//多点广播套接字
	private MulticastSocket socket = null;
	private InetAddress iaddress = null;
	
	//本机IP地址和IP地址的字节码
	public String IPconfig;
	private byte[] data;
	private int dataLen;
	
	
	//初始化事件
	private void initilizeEvent() {
		task=new Thread(this);
	}
	
	//初始化数据
	private void initilizeData(byte[] data) {
		try {
			IPconfig = InetAddress.getLocalHost().getHostAddress();
			iaddress = InetAddress.getByName(HOST);
			socket = new MulticastSocket(PORT);// 初始化多点广播
			socket.setTimeToLive(1);// 本地广播
			socket.joinGroup(iaddress);// 加入组
			ArrayList<Byte> tmpData=new ArrayList<Byte>();
			//加入用户识别码
			if(data!=null){
				for(byte b: data){
					tmpData.add(new Byte(b));
				}
			}
			//加入状态
			tmpData.add(new Byte((byte) 0));
			//加入地址
			byte[] tmpIP=IPconfig.getBytes();
			for(byte b:tmpIP){
				tmpData.add(new Byte(b));
			}
			//转换回数组
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
	
	//构造
	public UdpSender(byte[] data) {
		
		initilizeEvent();
		
		initilizeData(data);
	}
	
	//启动广播
	public void startService() {
		if(!isTaskRunning){
			if(task!=null){
				task=new Thread(this);
			}
			task.start();
		}
	}
	//关闭广播
	public void stopService() throws IOException {
		if(isTaskRunning){
			//关闭任务
			isTaskRunning=false;
			//广播一条关闭服务的消息
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
