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
 * BaseLAN����˻��࣬�����ľ����������������ʵ�־������ڿ���ͨ�źͶ�ͻ�������<br/>
 * <br>
 *����ʵ�ֵ�һ����Ҳ����ʵ��һ�Զ���߳����ӣ���Ҫע����ǣ����׼�ֻʵ�����ӹ��ܣ������ṩ���ݴ���ı�׼��Ҳ����˵
 *���������ݺʹ���������Ҫ��ȡ���ڲ����������������������ʹ�÷����뿴���������ṩ��ʾ����
 * <br>
 * @author AN
 * 
 *
 */
public class BaseLanClientServer {
	

	
	
	//״̬
	private 		boolean 									isConnected;//����״̬
	private 		ConnectionModel 					connectionModel;//����ģʽ
	private 		boolean 									isUpdServiceRunning;//udp����״̬
	private		boolean 									isServiceRunning;//�������ӷ���״̬
	//�ͻ���ʵ��
	private 		ArrayList<Socket>					clientSocketList=null;//��ͻ�������
	private 		ServerSocket 							serverSocket=null;//�����׽���
	private 		UdpSender 								sender;//UDPʵ��
	//������Ϣ
	private 		ArrayList<Message> 				clientMsgList=null;//�����ļ��б�
	private 		int 											port;//�˿�
	private  		String										hostName=InetAddress.getLocalHost().getHostName();//��������
	private 	 	String										localHostAddress=InetAddress.getLocalHost().getHostAddress();//������ַ
	
	
	
	
	
	//=================================
	// ==============����================
	//=================================
	//����isConnected��ֻ��
	/**
	 * ���ط��������״̬
	 * @return ����true��һ����һ�����ϵ�����
	 */
	public boolean getConnectState() {
		return isConnected;
	}
	
	
	
	
	
	//����isUpdServiceRunning��ֻ��
	/**
	 * ����UDP����״̬
	 * @return ����true��������
	 */
	public boolean getUdpServiceState() {
		return isUpdServiceRunning;
	}
	
	
	
	
	//����connectionModel��ֻ��
	/**
	 * ���ص�ǰ���������ģʽ
	 * @return
	 */
	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}
	
	
	
	
	//����isServiceRunning��ֻ��
	/**
	 * ���ط�������״̬
	 * @return
	 */
	public boolean getServiceState() {
		return isServiceRunning;
	}
	
	
	
	
	//����hostName��ֻ��
	/**
	 * ���ر��ؼ��������
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}
	
	
	
	
	
	//����localHostAddress��ֻ��
	/**
	 * ���ر���IP��ַ
	 * @return
	 */
	public String getLocalHostAddress() {
		return localHostAddress;
	}
	
	
	
	
	
	//����Port��ֻ��
	/**
	 * ���ذ󶨵Ķ˿�
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	
	
	
	/**
	 * ����UPD�㲥���͵�ʱ����10��999Ϊ��Чֵ
	 * @param value ����Ϊ��λ
	 * @throws ServiceException �ڷ�������ʱ�޸ĳ�ʱ���׳�����
	 */
	public void setUPDSendValue(long value) throws ServiceException{
		if(sender.isTaskRunning){
			throw new ServiceException("�������ڹ㲥��������ʱ��������");
		}
		if(sender!=null&&value>=10&&value<1000){
			sender.timeValue=value;
		}
	}
	
	
	
	
	/**
	 * ����UPD�Ķ˿�
	 * @param port
	 *//*
	public void setUDPSenderPort(int port) {
		if(sender!=null){
			sender.PORT=port;
		}
	}*/
	
	
	
	
	
	//����ClientMsgList��ֻ��
	/**
	 * ���������ļ��б�
	 * @return
	 */
	public ArrayList<Message> getClientMsg() {
		if(clientMsgList!=null){
			return clientMsgList;
		}
		return null;
	}
	
	
	
	
	
	//����ClientList��ֻ��
	/**
	 * �����Ѿ����ӵĿͻ���Socket
	 * @return
	 */
	public ArrayList<Socket> getClients() {
		if(clientSocketList!=null){
			return clientSocketList;
		}
		return null;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//=================˽�з���=============================
	//���ݳ�ʼ��
	private void initilize(int port,byte[] data) throws IOException{
		serverSocket=new ServerSocket(port);
		isConnected=false;
		isServiceRunning=false;
		this.port=port;
		clientSocketList=new ArrayList<Socket>();
	}
	
	
	
	
	
	//=====================���췽��====================
	/**
	 * ����һ���󶨶˿ڵķ���ServerSocket
	 * @param port ��Ҫ�󶨵Ķ˿�
	 * @throws IOException ʵ����ʧ�ܻ��׳��쳣
	 * @param model ����ģʽ
	 */
	public BaseLanClientServer(int port,ConnectionModel model) throws IOException {
		//��ʼ������
		initilize(port,null);
		sender=new UdpSender(null);
		connectionModel=model;
	}
	
	
	
	
	
	/**
	 * ����һ���Զ���UPD�㲥ʶ����ķ���ServiceSocket
	 * @param port ��Ҫ�󶨵Ķ˿�
	 * @param data �Զ����ʶ����
	 * @param model ����ģʽ
	 */
	public BaseLanClientServer(int port,byte[] data,ConnectionModel model) throws IOException {
		// TODO Auto-generated constructor stub
		initilize(port, data);
		sender=new UdpSender(data);
		connectionModel=model;
	}
	
	
	
	
	
	
	
	/**
	 * ���ص�ǰ�����ŵ�Socket����
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
	 * ================== ���񿪹�ϵ��=================
	 * ===========================================
	 */
	
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
	
	
	
	
	
	/**
	 * �����ͻ��˼������񣬼����ͻ��˵����󣬼���ģʽȡ����ʵ����ʱ�趨������ģʽ
	 * @param I �ص��ӿ�
	 * @return �����ɹ�����true
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public void startService(OnClientConnectListener I) throws ServiceException, IOException {
		if(isServiceRunning){
			throw new ServiceException("�ڲ��������²��ܶ�ε�������");
		}
		if(connectionModel==ConnectionModel.SingleConnection&&isConnected){
			throw new ServiceException("�Ѿ����ӵ��ͻ��ˣ����ȶϿ�����");
		}
		if(clientMsgList==null){
			clientMsgList=new ArrayList<Message>();
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
						clientSocketList.add(client);
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
						clientMsgList.add(clientMsg);
						
						//���ýӿ�
						I.onClientConnected(client,client.getInputStream(),client.getOutputStream(),clientMsg);
						if(connectionModel==ConnectionModel.SingleConnection){
							isServiceRunning=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
						isServiceRunning=false;//ֹͣ���ܷ���
					}
				}
			}
		}).start();
	}
	
	
	
	
	
	
	
	/**
	 * �Ͽ��ͻ��˵����ӣ����Ҵ������ӵ��豸�б���ɾ������
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
	 * �ر�Service�����̷߳���
	 */
	public void closeService(){
		isServiceRunning=false;
	}
	
	
	
	
	
	
	/**
	 * ����Ѿ����ӵĿͻ��ˣ������ͷ���Դ
	 * @throws ServiceException �ڷ�������ʱ��ջ��׳��쳣
	 * @throws IOException ���ر�ʱ����IO����
	 */
	public void clearConnectedClient() throws ServiceException, IOException{
		if(isServiceRunning){
			throw new ServiceException("Service����û��ֹͣ");
		}
		if(isConnected){//�ж�����
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







//UDP������������
class UdpSender implements Runnable{
	//����
	public int PORT=4001;//�˿�
	private static final String HOST = "224.255.10.0";//�㲥��
	//private static final int IP_BYTE_LENGTH=14;
	
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
