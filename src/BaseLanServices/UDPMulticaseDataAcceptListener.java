package BaseLanServices;

import java.net.DatagramPacket;

/**
 * 客户端广播数据接收的监听器，当广播接收服务启动后，接收到服务器传来<br/>
 * 的字节码，就会被监听器收到，并且回调这个接口中的{@code public boolean onMulticaseDataAccepted(byte[] recvData)}这个方法。
 * @author AN
 *
 */
interface UDPMulticaseDataAcceptListener {
	/**
	 * 监听器调用的回调函数，在数据收到之后。
	 * @param recvData 收到的字节码数组，用于解析是否通过
	 * @return 
	 */
	public boolean onMulticaseDataAccepted(DatagramPacket packet,LanConnectState state);
}
