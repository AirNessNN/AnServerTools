package BaseLanServices;

import java.net.DatagramPacket;

/**
 * �ͻ��˹㲥���ݽ��յļ����������㲥���շ��������󣬽��յ�����������<br/>
 * ���ֽ��룬�ͻᱻ�������յ������һص�����ӿ��е�{@code public boolean onMulticaseDataAccepted(byte[] recvData)}���������
 * @author AN
 *
 */
interface UDPMulticaseDataAcceptListener {
	/**
	 * ���������õĻص��������������յ�֮��
	 * @param recvData �յ����ֽ������飬���ڽ����Ƿ�ͨ��
	 * @return 
	 */
	public boolean onMulticaseDataAccepted(DatagramPacket packet,LanConnectState state);
}
