package BaseLanServices;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * BaseLanClient的连接事件回调接口，使用此类监听器必须要实现回调的所有方法。
 * <li>onClientConnected<br>当调用BaseLenClient.connect()之后，一旦客户端连接上服务器，就会回调此函数。<br/>
	  此方法用于连接之后的一些操作。</li>
 * <li>onClientDisconnect<br>当调用BaseLenClient.disconnect()之后，确认与服务器断开连接之后，就会回调此方法。<br/>
	  此方法用于断开连接之后的一些操作。</li>
 * @author AN
 *
 */
public interface ClientConnectionStateCallback {
	/**
	 * 当调用BaseLenClient.connect()之后，一旦客户端连接上服务器，就会回调此函数。<br/>
	 * 此方法用于连接之后的一些操作。
	 * @param client
	 */
	public void onClientConnected(Socket client,InputStream in,OutputStream out);
	/**
	 * 当调用BaseLenClient.disconnect()之后，确认与服务器断开连接之后，就会回调此方法。<br/>
	 * 此方法用于断开连接之后的一些操作。
	 */
	public void onClientDisconnect(Socket client);
}
