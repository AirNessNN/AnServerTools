package BaseLanServices;


/**
 * LAN Client收到服务器广播的监听器，用于BaseLanClient的广播接收器，监听服务器发送的状态消息，此监听器内容有两个：<br>
 * <ul>
 * 		<li>public boolean onServiceMsgReceived(ServiceDevice device)<br>用于添加设备前的确认，当客户端收到来自任何局域网广播组内的消息都会回调此函数，
 * 			  收到的消息属于不过滤的，所以会接收到重复消息，因此我们提供一个布尔值给用户返回，当确认此消息有效时，
 * 			  则会添加到设备列表，否则取消添加。参数列表中的device发出消息的设备，设备中包含地址和名字。
 * 		</li>
 * <br>
 * 		<li>public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state)<br>
 * 			用于通知服务器设备状态发生改变或者有新的服务器被发现，一旦设备列表中添加了新服务器或者服务器准备离线，就会调用此方法。<br>
 * 			在调用之前，已经将新设备添加到服务器列表，可以通过BaseLanClient或继承它的类
 * 			的对象中的getLanDevices()返回的ArrayList对象获取到已经在线的所有设备，当有服务器离线，并且在调用之前，将会删除在设备列表中的对象
 * 		</li>
 * </ul>
 * @author AN
 *
 */
public interface ServiceStateListener {
	/**
	 * 用于添加设备前的确认，当客户端收到来自任何局域网广播组内的消息都会回调此函数，
 * 			  收到的消息属于不过滤的，所以会接收到重复消息，因此我们提供一个布尔值给用户返回，当确认此消息有效时，
 * 			  则会添加到设备列表，否则取消添加。参数列表中的device发出消息的设备，设备中包含地址和名字。
	 * @param device
	 * @return
	 */
	public boolean onServiceMsgReceived(ServiceDevice device);
	
	/**
	 * 用于通知服务器设备状态发生改变或者有新的服务器被发现，一旦设备列表中添加了新服务器或者服务器准备离线，就会调用此方法。<br>
 * 			在调用之前，已经将新设备添加到服务器列表，可以通过BaseLanClient或继承它的类
 * 			的对象中的getLanDevices()返回的ArrayList对象获取到已经在线的所有设备，当有服务器离线，并且在调用之前，将会删除在设备列表中的对象
	 * @param listSize
	 * @param device
	 * @param state
	 */
	public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state);
}
