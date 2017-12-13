package BaseLanServices;

import java.io.Serializable;

/**
 * 消息类，可以序列化，用于连接上客户端之后传送的第一个配置文件，去填充用户信息
 * 
 * @author AN
 *
 */
public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户端名
	 */
	public String name;
	/**
	 * 设备地址
	 */
	public String host;
	/**
	 * 设备端口
	 */
	public int port;
	/**
	 * 设备名称
	 */
	public String computerName;

	/**
	 * 填充信息
	 * 
	 * @param name
	 * @param host
	 * @param port
	 * @param cpn
	 *            电脑名称
	 */
	public Message(String name, String host, int port, String cpn) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.computerName = cpn;
	}
	
	@Override
	public String toString() {
		return this.name+host;
	}
	
	@Override
	public boolean equals(Object obj) {
		Message message=(Message)obj;
		if(message.computerName!=computerName)
			return false;
		if(!message.host.equals(host))
			return false;
		if(port!=message.port)
			return false;
		return true;
	}
}