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
 * An�����������ͻ�����<br/>
 * �ü򵥵Ĳ���ʵ�־������ͻ��˵����Ӻ;�����IP�㲥��<br/>
 * <br/>
 * �ͻ��˷���������ʱ���ھ�����������UDP�㲥���գ��㲥����Ϊ�û�������ֽ��룬<br/>
 * ���ͻ����յ��ֽ���ʱ����ص��û�����Ľ���������ȥ��������˷�����Udp_Package<br/>
 * Ȼ��ͻ��˻��������ӵ�����ˡ�
 * <li>�÷���<br>ʵ����һ��BaseLANClient����֮�����úÿͻ������ƣ��󶨶˿ڣ�����creatNewSocket��
 * �ٵ���connect���ӣ��������ӵ���Ӧ�ķ������
 * ���ھ������ڿ���ʹ�ù㲥ϵͳ���յ������
 * ������ʶ���룬ͨ��ʶ�����÷���˵�IP��ַ����һ�������½�����֮��Ͽ���Ҫ����disconnect��
 * ������disconnect֮���Ѿ�������Ч�������ٴ�������Ҫ���µ���createNewSocket���ҵ���connect��
 * �رղ��ͷű�������Ҫ����closeAll����ȷ���Ѿ��ر��������Ӻͷ��񣬵���������֮��ö����Ѿ����ڿ��ã�ֻ��ʵ����һ���¶���</li>
 * 
 * @author AN
 *
 */
public class BaseLanClient{

	// ����
	public static final int 						DATA_LENGTH				=14;
	/**
	 * ������ʹ�õĶ˿ں�
	 */
	private int 										port 									= 4001; // �˿�

	//��Ա����
	private String 									name; // �ͻ�������
	private boolean 								isConnecting;
	private boolean								isSkipLocalHost				=false;
	private boolean								isClosed							=false;
	
	//����
	private UdpAccept 							udpAccept 						= null;// �㲥������
	private Socket 								client; // �ͻ��˳нӵ�Socket
	private SocketAddress 					address;
	private InputStream 						in 										= null;// ������
	private OutputStream 					out 									= null;// �����
	private ArrayList<ServiceDevice>	lanDevices 						= null;//�豸�б�
	
	private String 									hostName						=null;//��������
	
	
	//name���ԣ���д
	/**
	 * ��ñ��ͻ��˵�����
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * ���ÿͻ��˵����ƣ����ֵΪnull����ͻ�������Ĭ��Ϊ��������
	 * @param name
	 */
	public void setName(String name) {
		if(name==null){
			this.name=hostName;
		}else{
			this.name=name;
		}
	}
	
	
	
	
	//isSkipLocalHost���ԣ���д
	/**
	 * �����Ƿ�������������ģ�鷢����ʶ����
	 * @param b
	 */
	public void setSkipLocalHost(boolean b) {
		isSkipLocalHost=b;
	}
	/**
	 * ����һ������ֵ��ʾ�Ƿ���ձ�������˷�����ʶ����
	 * @return
	 */
	public boolean SkipLocalHost() {
		return isSkipLocalHost;
	}
	
	
	
	
	//isConnecting���ԣ�ֻ��
	/**
	 * ��õ�ǰ�ͻ�������״̬
	 * @return 
	 */
	public boolean getConnectState() {
		return isConnecting;
	}
	
	
	
	
	//port���ԣ�ֻ��
	/**
	 * ���ظ�����ʹ�õĶ˿ڣ��˿�ֻ����ʵ�������ߴ���������ʱ����
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	
	
	
	//lanDevices���ԣ�ֻ��
	/**
	 * �����Ѿ������ھ�����֮�е��豸IP
	 * @return HashSet
	 */
	public ArrayList<ServiceDevice> getLanDevices() {
		if(isClosed){
			return null;
		}
		return lanDevices;
	}

	
	
	
	
	//inputStream���ԣ�ֻ��
	/**
	 * ��÷���˵�������
	 * @return ����InputStream
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
	
	
	
	
	
	//outputStream���ԣ�ֻ��
	/**
	 * ���ط���˵������
	 * @return ����OutputStream
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
	
	
	
	
	
	
	

	//�ص��ӿ�
	/**
	 * �㲥���ݴ���ص������ڼ�������������״̬�Ļص������ַ�����֮������onServiceMsgReceived(ServiceDevice device)����������״̬�����ı�ʱ����ص�
	 * onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state)<br>
	 * ������ô˲���Ϊnull����ʹ��Ĭ�����
	 */
	public ServiceStateListener 				serviceStateListener 				= null;

	// �����ص�ʵ��
	/**
	 * ����״̬�����ص���������connect��disconnect��״̬�����ı䣬�ͻ��Զ�������Ӧ�ļ����������������Ϊnull���򲻼���
	 */
	public ClientConnectionStateCallback connectionStateCallback = null;

	// �㲥�����ص�
	private UDPMulticaseDataAcceptListener acceptListener = new UDPMulticaseDataAcceptListener() {

		@Override
		public boolean onMulticaseDataAccepted(DatagramPacket packet, LanConnectState state) {
			if (state == LanConnectState.DATA_ACCEPTED) {
				
				int flag=0;//1�ǵ���13�ֽڣ�δ�����û�ʶ���룬2����13�ֽ��Ƕ������û�ʶ���룬0��δʶ��ɹ�
				ServiceDevice device=null;
				device=Utils.getServiceDevice(packet.getData(), packet.getLength(),DATA_LENGTH);
				//�������ֽ���
				if (packet.getLength() >= DATA_LENGTH) {
					//�����û�������ֽ���
					if (serviceStateListener != null) {
						if (serviceStateListener.onServiceMsgReceived(device)) {
							flag=1;
						}
					}
				}
				
				// ���IP�Ƿ���ȷ
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

	
	
	
	
	
	// ��ʼ����������
	private void initialize() throws IOException {
		udpAccept = new UdpAccept(acceptListener);//��ʼ���㲥��
		lanDevices = new ArrayList<ServiceDevice>();//��ʼ���豸�б�
		hostName=InetAddress.getLocalHost().getHostName();//��ȡ��������
	}

	
	
	
	
	
	/**
	 * ��ʼ��һ���������ֵĿͻ��˶���ʹ��Ĭ�ϵ�UDP��������Ĭ�Ϲ㲥IP��ַ
	 * @throws IOException 
	 */
	public BaseLanClient() throws IOException {
		initialize();
	}

	
	
	
	
	/**
	 * ʵ����һ�������Զ���Ŀͻ��ˣ�ʵ���ڲ������Ӽ�������
	 * @param name �ͻ��˵����֣��ڷ��������ʾ���豸��
	 * @param connectionStateCallback ���Ӽ�����
	 * @param analyzeCallback ���������
	 * @throws IOException IO����
	 */
	public BaseLanClient(String name,ClientConnectionStateCallback connectionStateCallback,ServiceStateListener analyzeCallback)
			throws IOException {

		initialize();
		// ��ʼ���ص�����
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
	 * �������Ӽ�����
	 * 
	 * @param callback
	 *            �Ѿ�ʵ�ַ����Ļص�����
	 */
	public void setClientConnectionStateCallback(ClientConnectionStateCallback callback) {
		if (callback == null) {
			connectionStateCallback=null;
			return;
		}
		connectionStateCallback = callback;
	}

	
	
	
	
	/**
	 * ���÷��������
	 * @param I ʵ�ֵĽӿ�
	 */
	public void setServiceStateListener(ServiceStateListener I) {
		if(I!=null) {
			serviceStateListener=I;
			return ;
		}
		serviceStateListener=null;
	}
	
	
	

	
	
	
	
	
	/**
	 * ���������״̬�����̣߳������ض����ֽ��룬��������֮��<br/>
	 * �����ó���IPͬ�����ӵ�Ŀ�꣬���յ�һ�������Ͼͻ����<br/>
	 * {@code ServiceStateListener}�ķ�����
	 */
	public void startBroadcase() {
		/*
		 * ��ִ�й㲥���� ���յ��㲥���ֶκ���н��� Ȼ�󽫽����õ���IP�ַ���ȥ���ӷ����
		 */
		if (udpAccept == null) {
			return;
		}
		if (!udpAccept.isTaskRunning) {
			udpAccept.startService();
		}
	}

	
	
	
	
	
	/**
	 * �رչ㲥
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
	 * �ر����з��񣬲����ͷ�������Դ�����ͷű�������Դ֮ǰ��Ҫ����һ��closeAll �������������벻���Ľ��<br/>
	 * �رշ���ʱ�����������еķ��񣬻��׳������쳣
	 * 
	 * @throws ServiceException
	 *             ������������ʱ���׳�
	 * @throws IOException 
	 */
	public void closeAll() throws ServiceException, IOException {
		if (udpAccept != null) {
			if (udpAccept.isTaskRunning) {
				throw new ServiceException("udpAccept�����������С�");
			}
		}
		if(isConnecting){
			throw new ServiceException("Զ���豸���������С�");
		}
		//�رչ㲥
		udpAccept.stopService();
		udpAccept.close();
		udpAccept=null;
		//����б�
		if(lanDevices!=null){
			lanDevices.clear();
			lanDevices=null;
		}
		//�ر�����
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
	 * ����һ���¿ͻ���ʵ��������ָ����Ҫ�󶨵Ķ˿�
	 * @param port
	 * @throws ServiceException
	 */
	public void creatNewConnect(int port) throws ServiceException {
		if(isConnecting){
			throw new ServiceException("Զ���豸�������ӡ�");
		}
		client = new Socket();
		this.port=port;
		address=null;
		in=null;
		out=null;
	}
	
	
	
	
	
	/**
	 * ���ӵ��Ѿ�ʵ�����˵�Զ�̿ͻ��ˣ������������ӳ�ʱ
	 * @param IP ����Ŀ���IP��ַ
	 * @param timeout ��ʱ
	 * @return true��ʾ���ӵ�Ŀ�꣬false��ʾ����ʧ�ܻ��߳�ʱ
	 * @throws IOException
	 * @throws ServiceException
	 */
	public boolean connect(String IP,int timeout) throws IOException, ServiceException {
		address=new InetSocketAddress(IP, port);
		if(client==null){
			throw new ServiceException("Clientû�г�ʼ����");
		}
		client.connect(address, timeout);
		if(client.isConnected()){
			isConnecting = true;
			in = client.getInputStream();
			out = client.getOutputStream();
			// ���͵�һ������
			InetAddress address = InetAddress.getLocalHost();
			byte[] b=Utils.toByteArray(new Message(name, IP, port, address.getHostName().toString()));
			out.write(b);
			//�ص�
			connectionStateCallback.onClientConnected(client,in,out);
			return true;
		}
		return false;
	}

	
	
	
	
	
	/**
	 * �Ͽ����ӣ��ر�Client�������ͷ�clientSocket
	 * 
	 * @throws IOException �رչ���������IO������׳�
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
	 * ��û�з������е�����£��رտͻ���
	 * @throws ServiceException
	 * @throws IOException
	 */
	public void closeSocket() throws IOException {
		//�ر���
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
		//�رտͻ���
		if(client!=null){
			if(client.isConnected()){
				client.close();
			}
			client=null;
		}
		isConnecting=false;
	}
	
	
	
	
	
	
	/**
	 * ����豸�б������ͷ���Դ
	 */
	public void clearDeviceList() {
		//����б�
		synchronized (lanDevices) {
			if(lanDevices!=null){
				lanDevices.clear();
			}
		}
	}
	
	
	
	/**
	 * ���ط����豸�б�
	 * @return ������򷵻�null
	 */
	public ArrayList<ServiceDevice> getDevices() {
		if(lanDevices!=null) {
			return lanDevices;
		}
		return null;
	}
}











// UDP������������
class UdpAccept implements Runnable {
	// ����
	public static final int 									PORT = 4001;
	public static final String 								HOST = "224.255.10.0";

	// ��Ա����
	public boolean 											isTaskRunning;// �㲥�߳�

	// �ص����ݶ���
	private UDPMulticaseDataAcceptListener 	I = null;

	// ����
	private Thread 											task = null;

	// ���㲥�׽���
	private MulticastSocket 								socket = null;
	private InetAddress 									group;// ����InteAddress

	
	
	
	
	
	public UdpAccept(UDPMulticaseDataAcceptListener I) throws IOException {
		// ��ʼ��
		isTaskRunning = false;
		socket = new MulticastSocket(PORT);
		group = InetAddress.getByName(HOST);
		socket.joinGroup(group);
		if (I != null) {
			this.I = I;
		}
	}

	
	
	
	
	
	/**
	 * �ر�Socket�������������ӣ������ͷ���Դ
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
			isTaskRunning = !isTaskRunning;// ��������״̬
		}
		// �ͷ�������Դ
		socket = null;
		task = null;
	}

	
	
	
	
	
	/**
	 * �����㲥����
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
	 * ֹͣ����UPD�㲥����
	 */
	public void stopService() {
		if (task == null) {
			return;
		}
		if (task.isAlive()) {
			isTaskRunning = false;
		}
	}

	
	
	
	
	
	//��������
	@Override
	public void run() {
		// ʵ������Ҫ���ܵİ�
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			while (isTaskRunning) {
				socket.receive(packet);
				if (I != null) {
					I.onMulticaseDataAccepted(packet, LanConnectState.DATA_ACCEPTED);// �ص�����
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (I != null) {
				I.onMulticaseDataAccepted(null, LanConnectState.DATA_ERROR);// �ص�����
			}
			e.printStackTrace();
			return;
		}
	}

}
