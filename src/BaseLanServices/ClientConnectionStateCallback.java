package BaseLanServices;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * BaseLanClient�������¼��ص��ӿڣ�ʹ�ô������������Ҫʵ�ֻص������з�����
 * <li>onClientConnected<br>������BaseLenClient.connect()֮��һ���ͻ��������Ϸ��������ͻ�ص��˺�����<br/>
	  �˷�����������֮���һЩ������</li>
 * <li>onClientDisconnect<br>������BaseLenClient.disconnect()֮��ȷ����������Ͽ�����֮�󣬾ͻ�ص��˷�����<br/>
	  �˷������ڶϿ�����֮���һЩ������</li>
 * @author AN
 *
 */
public interface ClientConnectionStateCallback {
	/**
	 * ������BaseLenClient.connect()֮��һ���ͻ��������Ϸ��������ͻ�ص��˺�����<br/>
	 * �˷�����������֮���һЩ������
	 * @param client
	 */
	public void onClientConnected(Socket client,InputStream in,OutputStream out);
	/**
	 * ������BaseLenClient.disconnect()֮��ȷ����������Ͽ�����֮�󣬾ͻ�ص��˷�����<br/>
	 * �˷������ڶϿ�����֮���һЩ������
	 */
	public void onClientDisconnect(Socket client);
}
