package BaseLanServices;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * LAN Service已连接的监听器<br/>
 * <br>
 * 调用onClientConnected方法的的条件是客户端已经connect到此服务器的socket，
 * <br>
 * 并且已经接收到客户端连接的第一条信息。
 * <br>此方法回传两个参数{@code Socket}    {@code Message}
 * <br>
 * <br>
 * 回调此接口方法的类有：
 * <li>
 * 	<ul>BaseLanService类</ul>
 * </li>
 * 具体信息可以参考:
 * @see BaseLanClientServer
 * @author AN
 * 
 *
 */
public interface OnClientConnectListener {
	public void onClientConnected(Socket client,InputStream in,OutputStream out,Message message);
}
