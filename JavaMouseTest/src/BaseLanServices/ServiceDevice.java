package BaseLanServices;

/**
 * BaseLANService��UPD����װ���࣬�����֤��Ϣ��ʶ�����IP��ַ���������ͨ���ɷ���˷��������ڿͻ���ʶ���������ݺ�������ַ��
 * @author AN
 *
 */
public final class ServiceDevice {
	private byte[] deviceName=null;
	private byte[] deviceHost=null;
	private byte state;
	
	/**
	 * ʵ����һ��������UDP����װ���ࡣ
	 * @param deviceName ʶ���������
	 * @param deviceHost IP��ַ������
	 * @param state ״̬
	 */
	public ServiceDevice(byte[] deviceName,byte[] deviceHost,byte state) {
		// TODO Auto-generated constructor stub
		this.deviceHost=deviceHost;
		this.deviceName=deviceName;
		this.state=state;
	}
	
	/**
	 * ��÷�������IP��ַ
	 * @return �����ַ���
	 */
	public String getHost(){
		return new String(deviceHost,0,13);
	}
	
	/**
	 * ���һ���ɷ�����������ʶ�������������
	 * @return byte[]
	 */
	public byte[] getCode(){
		return deviceName;
	}
	
	/**
	 * ��÷�����״̬
	 * @return 
	 */
	public byte getState(){
		return state;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		ServiceDevice device=(ServiceDevice)obj;
		//�Ա�ʶ����
		if(!device.getHost().equals(this.getHost())){
			return false;
		}
		//�Աȳ���
		if(device.getCode().length!=this.deviceName.length){
			return false;
		}
		//�Ա�IP
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
