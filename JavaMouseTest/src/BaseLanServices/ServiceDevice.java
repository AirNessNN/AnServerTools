package BaseLanServices;

/**
 * BaseLANService的UPD服务装箱类，存放验证信息的识别码和IP地址，此类对象通常由服务端发出，用于客户端识别服务端数据和主机地址。
 * @author AN
 *
 */
public final class ServiceDevice {
	private byte[] deviceName=null;
	private byte[] deviceHost=null;
	private byte state;
	
	/**
	 * 实例化一个服务器UDP数据装箱类。
	 * @param deviceName 识别码二进制
	 * @param deviceHost IP地址二进制
	 * @param state 状态
	 */
	public ServiceDevice(byte[] deviceName,byte[] deviceHost,byte state) {
		// TODO Auto-generated constructor stub
		this.deviceHost=deviceHost;
		this.deviceName=deviceName;
		this.state=state;
	}
	
	/**
	 * 获得服务器的IP地址
	 * @return 返回字符串
	 */
	public String getHost(){
		return new String(deviceHost,0,13);
	}
	
	/**
	 * 获得一个由服务器发出的识别码二进制数组
	 * @return byte[]
	 */
	public byte[] getCode(){
		return deviceName;
	}
	
	/**
	 * 获得服务器状态
	 * @return 
	 */
	public byte getState(){
		return state;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		ServiceDevice device=(ServiceDevice)obj;
		//对比识别码
		if(!device.getHost().equals(this.getHost())){
			return false;
		}
		//对比长度
		if(device.getCode().length!=this.deviceName.length){
			return false;
		}
		//对比IP
		for(int i=0;i<device.getCode().length;i++){
			if(device.deviceName[i]!=this.deviceName[i]){
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		return new String(deviceName)+getHost();
	}

}
