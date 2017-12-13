package BaseLanServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An基础局域网客户端类<br/>
 * 用简单的步骤实现局域网客户端的连接和局域网IP广播。<br/>
 * <br/>
 * 客户端服务启动的时候，在局域网内启动UDP广播接收，广播内容为用户定义的字节码，<br/>
 * 当客户端收到字节码时，会回调用户定义的解析方法，去解析服务端发出的Udp_Package<br/>
 * 然后客户端会主动连接到服务端。
 * <li>用法：<br>实例化一个BaseLANClient对象之后，配置好客户端名称，绑定端口，调用creatNewSocket，
 * 再调用connect连接，可以连接到相应的服务组件
 * （在局域网内可以使用广播系统接收到服务端
 * 传来的识别码，通过识别码获得服务端的IP地址），一个连接新建出来之后断开需要调用disconnect，
 * 当连接disconnect之后已经不再有效，所以再次连接需要重新调用createNewSocket并且调用connect，
 * 关闭并释放本对象需要调用closeAll并且确保已经关闭所有连接和服务，当对象销毁之后该对象已经不在可用，只能实例化一个新对象</li>
 * 
 * @author AN
 *
 */
public class BaseLanClient{

	// 属性
	public static final int 						DATA_LENGTH				=14;
	/**
	 * 连接所使用的端口号
	 */
	private int 										port 									= 4001; // 端口

	//成员变量
	private String 									name; // 客户端名称
	private boolean 								isConnecting;
	private boolean								isSkipLocalHost				=false;
	private boolean								isClosed							=false;
	
	//配置
	private UdpAccept 							udpAccept 						= null;// 广播接收器
	private Socket 								client; // 客户端承接的Socket
	private SocketAddress 					address;
	private InputStream 						in 										= null;// 输入流
	private OutputStream 					out 									= null;// 输出流
	private ArrayList<ServiceDevice>	lanDevices 						= null;//设备列表
	
	private String 									hostName						=null;//本机名字
	
	
	//name属性：读写
	/**
	 * 获得本客户端的名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置客户端的名称，如果值为null，则客户端名称默认为本机名称
	 * @param name
	 */
	public void setName(String name) {
		if(name==null){
			this.name=hostName;
		}else{
			this.name=name;
		}
	}
	
	
	
	
	//isSkipLocalHost属性：读写
	/**
	 * 设置是否跳过本机服务模组发出的识别码
	 * @param b
	 */
	public void setSkipLocalHost(boolean b) {
		isSkipLocalHost=b;
	}
	/**
	 * 返回一个布尔值表示是否接收本机服务端发出的识别码
	 * @return
	 */
	public boolean SkipLocalHost() {
		return isSkipLocalHost;
	}
	
	
	
	
	//isConnecting属性：只读
	/**
	 * 获得当前客户端连接状态
	 * @return 
	 */
	public boolean getConnectState() {
		return isConnecting;
	}
	
	
	
	
	//port属性：只读
	/**
	 * 返回该类所使用的端口，端口只能在实例化或者创建新连接时设置
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	
	
	
	//lanDevices属性：只读
	/**
	 * 返回已经存在于局域网之中的设备IP
	 * @return HashSet
	 */
	public ArrayList<ServiceDevice> getLanDevices() {
		if(isClosed){
			return null;
		}
		return lanDevices;
	}

	
	
	
	
	//inputStream属性：只读
	/**
	 * 获得服务端的输入流
	 * @return 返回InputStream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		if(isClosed){
			return null;
		}
		if(client!=null){
			return client.getInputStream();
		}
		return null;
	}
	
	
	
	
	
	//outputStream属性：只读
	/**
	 * 返回服务端的输出流
	 * @return 返回OutputStream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		if(isClosed){
			return null;
		}
		if(client!=null){
			return client.getOutputStream();
		}
		return null;
	}
	
	
	
	
	
	
	

	//回调接口
	/**
	 * 广播数据处理回调，用于监听服务器服务状态的回调，发现服务器之后会调用onServiceMsgReceived(ServiceDevice device)，当服务器状态发生改变时，会回调
	 * onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state)<br>
	 * 如果设置此参数为null，则使用默认添加
	 */
	public ServiceStateListener 				serviceStateListener 				= null;

	// 监听回调实现
	/**
	 * 连接状态监听回调，当调用connect或disconnect后，状态发生改变，就会自动调用相应的监听方法。如果设置为null，则不监听
	 */
	public ClientConnectionStateCallback connectionStateCallback = null;

	// 广播监听回调
	private UDPMulticaseDataAcceptListener acceptListener = new UDPMulticaseDataAcceptListener() {

		@Override
		public boolean onMulticaseDataAccepted(DatagramPacket packet, LanConnectState state) {
			if (state == LanConnectState.DATA_ACCEPTED) {
				
				int flag=0;//1是等于13字节，未定义用户识别码，2大于13字节是定义了用户识别码，0是未识别成功
				ServiceDevice device=null;
				device=Utils.getServiceDevice(packet.getData(), packet.getLength(),DATA_LENGTH);
				//定义了字节码
				if (packet.getLength() >= DATA_LENGTH) {
					//传回用户定义的字节码
					if (serviceStateListener != null) {
						if (serviceStateListener.onServiceMsgReceived(device)) {
							flag=1;
						}
					}
				}
				
				// 检测IP是否正确
				if (device!=null) {
					try {
						if(isSkipLocalHost&&device.getHost().equals(InetAddress.getLocalHost().getHostAddress())){
							return false;
						}
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(flag!=0){
						synchronized (lanDevices) {
							if(!lanDevices.contains(device)){
								lanDevices.add(device);
								if(serviceStateListener!=null){
									serviceStateListener.onServiceDeviceStateChanged(lanDevices.size(),device,device.getState());
								}
							}else{
								if(device.getState()==ServiceState.SERVICE_IS_STOP){
									lanDevices.remove(device);
									if(serviceStateListener!=null){
										serviceStateListener.onServiceDeviceStateChanged(lanDevices.size(),device,device.getState());
									}
								}
							}
						}
					}
				}
			}
			return false;
		}
	};

	
	
	
	
	
	// 初始化部分数据
	private void initialize() throws IOException {
		udpAccept = new UdpAccept(acceptListener);//初始化广播器
		lanDevices = new ArrayList<ServiceDevice>();//初始化设备列表
		hostName=InetAddress.getLocalHost().getHostName();//获取本机名称
	}

	
	
	
	
	
	/**
	 * 初始化一个不带名字的客户端对象，使用默认的UDP接收器，默认广播IP地址
	 * @throws IOException 
	 */
	public BaseLanClient() throws IOException {
		initialize();
	}

	
	
	
	
	/**
	 * 实例化一个名称自定义的客户端，实现内部的连接监听方法
	 * @param name 客户端的名字，在服务端中显示的设备名
	 * @param connectionStateCallback 连接监听器
	 * @param analyzeCallback 服务监听器
	 * @throws IOException IO错误
	 */
	public BaseLanClient(String name,ClientConnectionStateCallback connectionStateCallback,ServiceStateListener analyzeCallback)
			throws IOException {

		initialize();
		// 初始化回调对象
		if(analyzeCallback!=null){
			this.serviceStateListener = analyzeCallback;
		}
		if(connectionStateCallback!=null){
			this.connectionStateCallback = connectionStateCallback;
		}
		if(name!=null){
			this.name=name;
		}
	}

	
	
	
	
	
	/**
	 * 设置连接监听器
	 * 
	 * @param callback
	 *            已经实现方法的回调函数
	 */
	public void setClientConnectionStateCallback(ClientConnectionStateCallback callback) {
		if (callback == null) {
			connectionStateCallback=null;
			return;
		}
		connectionStateCallback = callback;
	}

	
	
	
	
	/**
	 * 设置服务监听器
	 * @param I 实现的接口
	 */
	public void setServiceStateListener(ServiceStateListener I) {
		if(I!=null) {
			serviceStateListener=I;
			return ;
		}
		serviceStateListener=null;
	}
	
	
	

	
	
	
	
	
	/**
	 * 开启服务端状态监听线程，接收特定的字节码，解析数据之后，<br/>
	 * 分析得出的IP同步连接到目标，接收到一旦连接上就会调用<br/>
	 * {@code ServiceStateListener}的方法。
	 */
	public void startBroadcase() {
		/*
		 * 先执行广播接受 接收到广播的字段后进行解析 然后将解析得到的IP字符串去连接服务端
		 */
		if (udpAccept == null) {
			return;
		}
		if (!udpAccept.isTaskRunning) {
			udpAccept.startService();
		}
	}

	
	
	
	
	
	/**
	 * 关闭广播
	 */
	public void stopBroadcase() {
		if (udpAccept == null) {
			return;
		}
		if (udpAccept.isTaskRunning) {
			udpAccept.stopService();
		}
	}

	
	
	
	
	
	/**
	 * 关闭所有服务，并且释放所有资源，在释放本对象资源之前需要调用一次closeAll ，否则会出现意想不到的结果<br/>
	 * 关闭服务时遇到正在运行的服务，会抛出以下异常
	 * 
	 * @throws ServiceException
	 *             服务正在运行时会抛出
	 * @throws IOException 
	 */
	public void closeAll() throws ServiceException, IOException {
		if (udpAccept != null) {
			if (udpAccept.isTaskRunning) {
				throw new ServiceException("udpAccept服务正在运行。");
			}
		}
		if(isConnecting){
			throw new ServiceException("远程设备正在连接中。");
		}
		//关闭广播
		udpAccept.stopService();
		udpAccept.close();
		udpAccept=null;
		//清空列表
		if(lanDevices!=null){
			lanDevices.clear();
			lanDevices=null;
		}
		//关闭连接
		if(in!=null) {
			in.close();
			in=null;
		}
		if(out!=null) {
			out.close();
			out=null;
		}
		client.close();
		client=null;
	}

	
	
	
	/**
	 * 创建一个新客户端实例，并且指定需要绑定的端口
	 * @param port
	 * @throws ServiceException
	 */
	public void creatNewConnect(int port) throws ServiceException {
		if(isConnecting){
			throw new ServiceException("远程设备正在连接。");
		}
		client = new Socket();
		this.port=port;
		address=null;
		in=null;
		out=null;
	}
	
	
	
	
	
	/**
	 * 连接到已经实例化了的远程客户端，并且设置连接超时
	 * @param IP 连接目标的IP地址
	 * @param timeout 超时
	 * @return true表示连接到目标，false表示连接失败或者超时
	 * @throws IOException
	 * @throws ServiceException
	 */
	public boolean connect(String IP,int timeout) throws IOException, ServiceException {
		address=new InetSocketAddress(IP, port);
		if(client==null){
			throw new ServiceException("Client没有初始化。");
		}
		client.connect(address, timeout);
		if(client.isConnected()){
			isConnecting = true;
			in = client.getInputStream();
			out = client.getOutputStream();
			// 发送第一个数据
			InetAddress address = InetAddress.getLocalHost();
			byte[] b=Utils.toByteArray(new Message(name, IP, port, address.getHostName().toString()));
			out.write(b);
			//回调
			connectionStateCallback.onClientConnected(client,in,out);
			return true;
		}
		return false;
	}

	
	
	
	
	
	/**
	 * 断开连接，关闭Client，并不释放clientSocket
	 * 
	 * @throws IOException 关闭过程中遇到IO错误会抛出
	 */
	public void disconnect() throws IOException {
		if (client == null) {
			connectionStateCallback.onClientDisconnect(null);
			return;
		}
		client.close();
		connectionStateCallback.onClientDisconnect(client);
		isConnecting = false;
	}
	
	
	
	
	
	/**
	 * 在没有服务运行的情况下，关闭客户端
	 * @throws ServiceException
	 * @throws IOException
	 */
	public void closeSocket() throws IOException {
		//关闭流
		if(in!=null){
			in.close();
		}else{
			in=null;
		}
		if(out!=null){
			out.close();
		}else{
			out=null;
		}
		//关闭客户端
		if(client!=null){
			if(client.isConnected()){
				client.close();
			}
			client=null;
		}
		isConnecting=false;
	}
	
	
	
	
	
	
	/**
	 * 清空设备列表，并不释放资源
	 */
	public void clearDeviceList() {
		//清空列表
		synchronized (lanDevices) {
			if(lanDevices!=null){
				lanDevices.clear();
			}
		}
	}
	
	
	
	/**
	 * 返回服务设备列表
	 * @return 如果空则返回null
	 */
	public ArrayList<ServiceDevice> getDevices() {
		if(lanDevices!=null) {
			return lanDevices;
		}
		return null;
	}
}











// UDP局域网接收器
class UdpAccept implements Runnable {
	// 常量
	public static final int 									PORT = 4001;
	public static final String 								HOST = "224.255.10.0";

	// 成员变量
	public boolean 											isTaskRunning;// 广播线程

	// 回调内容对象
	private UDPMulticaseDataAcceptListener 	I = null;

	// 任务
	private Thread 											task = null;

	// 多点广播套接字
	private MulticastSocket 								socket = null;
	private InetAddress 									group;// 声明InteAddress

	
	
	
	
	
	public UdpAccept(UDPMulticaseDataAcceptListener I) throws IOException {
		// 初始化
		isTaskRunning = false;
		socket = new MulticastSocket(PORT);
		group = InetAddress.getByName(HOST);
		socket.joinGroup(group);
		if (I != null) {
			this.I = I;
		}
	}

	
	
	
	
	
	/**
	 * 关闭Socket和它的所有连接，并且释放资源
	 */
	public void close() {
		if (socket == null) {
			return;
		}
		if (socket.isClosed()) {
			return;
		}
		if (socket.isBound()) {
			socket.close();
			isTaskRunning = !isTaskRunning;// 更改运行状态
		}
		// 释放所有资源
		socket = null;
		task = null;
	}

	
	
	
	
	
	/**
	 * 启动广播收听
	 */
	public void startService() {
		if (task == null) {
			task = new Thread(this);
		}
		if (task.isAlive()) {
			return;
		}
		task.start();
		isTaskRunning = true;
	}

	
	
	
	
	
	/**
	 * 停止接受UPD广播数据
	 */
	public void stopService() {
		if (task == null) {
			return;
		}
		if (task.isAlive()) {
			isTaskRunning = false;
		}
	}

	
	
	
	
	
	//任务内容
	@Override
	public void run() {
		// 实例化将要接受的包
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			while (isTaskRunning) {
				socket.receive(packet);
				if (I != null) {
					I.onMulticaseDataAccepted(packet, LanConnectState.DATA_ACCEPTED);// 回调函数
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (I != null) {
				I.onMulticaseDataAccepted(null, LanConnectState.DATA_ERROR);// 回调函数
			}
			e.printStackTrace();
			return;
		}
	}

}
