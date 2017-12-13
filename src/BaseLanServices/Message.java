package BaseLanServices;

import java.io.Serializable;

/**
 * ��Ϣ�࣬�������л������������Ͽͻ���֮���͵ĵ�һ�������ļ���ȥ����û���Ϣ
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
	 * �ͻ�����
	 */
	public String name;
	/**
	 * �豸��ַ
	 */
	public String host;
	/**
	 * �豸�˿�
	 */
	public int port;
	/**
	 * �豸����
	 */
	public String computerName;

	/**
	 * �����Ϣ
	 * 
	 * @param name
	 * @param host
	 * @param port
	 * @param cpn
	 *            ��������
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