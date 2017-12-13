package BaseLanServices;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * LAN Service�����ӵļ�����<br/>
 * <br>
 * ����onClientConnected�����ĵ������ǿͻ����Ѿ�connect���˷�������socket��
 * <br>
 * �����Ѿ����յ��ͻ������ӵĵ�һ����Ϣ��
 * <br>�˷����ش���������{@code Socket}    {@code Message}
 * <br>
 * <br>
 * �ص��˽ӿڷ��������У�
 * <li>
 * 	<ul>BaseLanService��</ul>
 * </li>
 * ������Ϣ���Բο�:
 * @see BaseLanClientServer
 * @author AN
 * 
 *
 */
public interface OnClientConnectListener {
	public void onClientConnected(Socket client,InputStream in,OutputStream out,Message message);
}
