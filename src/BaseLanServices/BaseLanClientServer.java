package BaseLanServices;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * BaseLAN服务端基类，基本的局域网服务器组件，实现局域网内快速通信和多客户端连接<br/>
 * <br>
 *可以实现单一连接也可以实现一对多多线程连接，需要注意的是，此套件只实现连接功能，并不提供数据传输的标准，也就是说
 *，传输数据和传输内容需要获取到内部的输入流和输出流，具体使用方法请看各个方法提供的示例。
 * <br>
 * @author AN
 * 
 *
 */
public class BaseLanClientServer {
	

	
	
	//状态
	private 		boolean 									isConnected;//连接状态
	private 		ConnectionModel 					connectionModel;//连接模式
	private 		boolean 									isUpdServiceRunning;//udp服务状态
	private		boolean 									isServiceRunning;//并发连接服务状态
	//客户端实例
	private 		ArrayList<Socket>					clientSocketList=null;//多客户端数组
	private 		ServerSocket 							serverSocket=null;//服务套接字
	private 		UdpSender 								sender;//UDP实例
	//配置信息
	private 		ArrayList<Message> 				clientMsgList=null;//配置文件列表
	private 		int 											port;//端口
	private  		String										hostName=InetAddress.getLocalHost().getHostName();//本机名字
	private 	 	String										localHostAddress=InetAddress.getLocalHost().getHostAddress();//本机地址
	
	
	
	
	
	//=================================
	// ==============属性================
	//=================================
	//属性isConnected：只读
	/**
	 * 返回服务端连接状态
	 * @return 返回true有一个或一个以上的连接
	 */
	public boolean getConnectState() {
		return isConnected;
	}
	
	
	
	
	
	//属性isUpdServiceRunning：只读
	/**
	 * 返回UDP服务状态
	 * @return 返回true正在运行
	 */
	public boolean getUdpServiceState() {
		return isUpdServiceRunning;
	}
	
	
	
	
	//属性connectionModel：只读
	/**
	 * 返回当前对象的连接模式
	 * @return
	 */
	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}
	
	
	
	
	//属性isServiceRunning：只读
	/**
	 * 返回服务运行状态
	 * @return
	 */
	public boolean getServiceState() {
		return isServiceRunning;
	}
	
	
	
	
	//属性hostName：只读
	/**
	 * 返回本地计算机名称
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}
	
	
	
	
	
	//属性localHostAddress：只读
	/**
	 * 返回本机IP地址
	 * @return
	 */
	public String getLocalHostAddress() {
		return localHostAddress;
	}
	
	
	
	
	
	//属性Port：只读
	/**
	 * 返回绑定的端口
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	
	
	
	/**
	 * 设置UPD广播发送的时间间隔10到999为有效值
	 * @param value 毫秒为单位
	 * @throws ServiceException 在服务运行时修改超时会抛出错误
	 */
	public void setUPDSendValue(long value) throws ServiceException{
		if(sender.isTaskRunning){
			throw new ServiceException("不可以在广播服务运行时更改数据");
		}
		if(sender!=null&&value>=10&&value<1000){
			sender.timeValue=value;
		}
	}
	
	
	
	
	/**
	 * 设置UPD的端口
	 * @param port
	 *//*
	public void setUDPSenderPort(int port) {
		if(sender!=null){
			sender.PORT=port;
		}
	}*/
	
	
	
	
	
	//属性ClientMsgList：只读
	/**
	 * 返回配置文件列表
	 * @return
	 */
	public ArrayList<Message> getClientMsg() {
		if(clientMsgList!=null){
			return clientMsgList;
		}
		return null;
	}
	
	
	
	
	
	//属性ClientList：只读
	/**
	 * 返回已经连接的客户端Socket
	 * @return
	 */
	public ArrayList<Socket> getClients() {
		if(clientSocketList!=null){
			return clientSocketList;
		}
		return null;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//=================私有方法=============================
	//数据初始化
	private void initilize(int port,byte[] data) throws IOException{
		serverSocket=new ServerSocket(port);
		isConnected=false;
		isServiceRunning=false;
		this.port=port;
		clientSocketList=new ArrayList<Socket>();
	}
	
	
	
	
	
	//=====================构造方法====================
	/**
	 * 构造一个绑定端口的服务ServerSocket
	 * @param port 需要绑定的端口
	 * @throws IOException 实例化失败会抛出异常
	 * @param model 连接模式
	 */
	public BaseLanClientServer(int port,ConnectionModel model) throws IOException {
		//初始化数据
		initilize(port,null);
		sender=new UdpSender(null);
		connectionModel=model;
	}
	
	
	
	
	
	/**
	 * 构造一个自定义UPD广播识别码的服务ServiceSocket
	 * @param port 需要绑定的端口
	 * @param data 自定义的识别码
	 * @param model 连接模式
	 */
	public BaseLanClientServer(int port,byte[] data,ConnectionModel model) throws IOException {
		// TODO Auto-generated constructor stub
		initilize(port, data);
		sender=new UdpSender(data);
		connectionModel=model;
	}
	
	
	
	
	
	
	
	/**
	 * 返回当前连接着的Socket数量
	 * @return int count
	 */
	public int getConnectedCount() {
		int count=0;
		if(clientSocketList.size()>0){
			for(Socket socket : clientSocketList){
				if(socket.isConnected()){
					count++;
				}
			}
		}
		return count;
	}
	
	
	
	
	/*
	 * ===========================================
	 * ================== 服务开关系列=================
	 * ===========================================
	 */
	
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
	
	
	
	
	
	/**
	 * 开启客户端监听服务，监听客户端的请求，监听模式取决于实例化时设定的连接模式
	 * @param I 回调接口
	 * @return 启动成功返回true
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public void startService(OnClientConnectListener I) throws ServiceException, IOException {
		if(isServiceRunning){
			throw new ServiceException("在并发连接下不能多次调用连接");
		}
		if(connectionModel==ConnectionModel.SingleConnection&&isConnected){
			throw new ServiceException("已经连接到客户端，请先断开连接");
		}
		if(clientMsgList==null){
			clientMsgList=new ArrayList<Message>();
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
						clientSocketList.add(client);
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
						clientMsgList.add(clientMsg);
						
						//调用接口
						I.onClientConnected(client,client.getInputStream(),client.getOutputStream(),clientMsg);
						if(connectionModel==ConnectionModel.SingleConnection){
							isServiceRunning=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
						isServiceRunning=false;//停止接受服务
					}
				}
			}
		}).start();
	}
	
	
	
	
	
	
	
	/**
	 * 断开客户端的连接，并且从已连接的设备列表中删除该项
	 * @param socket
	 * @throws IOException 
	 */
	public void disconnectClient(Socket socket) throws IOException {
		if(socket!=null){
			socket.close();
			clientSocketList.remove(socket);
		}
	}
	
	
	
	
	
	
	
	/**
	 * 关闭Service监听线程服务
	 */
	public void closeService(){
		isServiceRunning=false;
	}
	
	
	
	
	
	
	/**
	 * 清空已经连接的客户端，并且释放资源
	 * @throws ServiceException 在服务运行时清空会抛出异常
	 * @throws IOException 当关闭时出现IO错误
	 */
	public void clearConnectedClient() throws ServiceException, IOException{
		if(isServiceRunning){
			throw new ServiceException("Service服务没有停止");
		}
		if(isConnected){//判断连接
			if(clientMsgList!=null){
				for(Socket socket :clientSocketList){
					if(socket.isConnected()){
						socket.close();
					}
				}
				if(clientSocketList.size()>0){
					clientSocketList.clear();
				}
			}
		}
		isConnected=false;
	}
}







//UDP局域网发送器
class UdpSender implements Runnable{
	//常量
	public int PORT=4001;//端口
	private static final String HOST = "224.255.10.0";//广播组
	//private static final int IP_BYTE_LENGTH=14;
	
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
				synchronized (socket) {
					socket.send(packet);
				}
				Thread.sleep(timeValue);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
