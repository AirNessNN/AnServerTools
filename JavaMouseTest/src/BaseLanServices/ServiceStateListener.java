package BaseLanServices;


/**
 * LAN Client�յ��������㲥�ļ�����������BaseLanClient�Ĺ㲥���������������������͵�״̬��Ϣ���˼�����������������<br>
 * <ul>
 * 		<li>public boolean onServiceMsgReceived(ServiceDevice device)<br>��������豸ǰ��ȷ�ϣ����ͻ����յ������κξ������㲥���ڵ���Ϣ����ص��˺�����
 * 			  �յ�����Ϣ���ڲ����˵ģ����Ի���յ��ظ���Ϣ����������ṩһ������ֵ���û����أ���ȷ�ϴ���Ϣ��Чʱ��
 * 			  �����ӵ��豸�б�����ȡ����ӡ������б��е�device������Ϣ���豸���豸�а�����ַ�����֡�
 * 		</li>
 * <br>
 * 		<li>public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state)<br>
 * 			����֪ͨ�������豸״̬�����ı�������µķ����������֣�һ���豸�б���������·��������߷�����׼�����ߣ��ͻ���ô˷�����<br>
 * 			�ڵ���֮ǰ���Ѿ������豸��ӵ��������б�����ͨ��BaseLanClient��̳�������
 * 			�Ķ����е�getLanDevices()���ص�ArrayList�����ȡ���Ѿ����ߵ������豸�����з��������ߣ������ڵ���֮ǰ������ɾ�����豸�б��еĶ���
 * 		</li>
 * </ul>
 * @author AN
 *
 */
public interface ServiceStateListener {
	/**
	 * ��������豸ǰ��ȷ�ϣ����ͻ����յ������κξ������㲥���ڵ���Ϣ����ص��˺�����
 * 			  �յ�����Ϣ���ڲ����˵ģ����Ի���յ��ظ���Ϣ����������ṩһ������ֵ���û����أ���ȷ�ϴ���Ϣ��Чʱ��
 * 			  �����ӵ��豸�б�����ȡ����ӡ������б��е�device������Ϣ���豸���豸�а�����ַ�����֡�
	 * @param device
	 * @return
	 */
	public boolean onServiceMsgReceived(ServiceDevice device);
	
	/**
	 * ����֪ͨ�������豸״̬�����ı�������µķ����������֣�һ���豸�б���������·��������߷�����׼�����ߣ��ͻ���ô˷�����<br>
 * 			�ڵ���֮ǰ���Ѿ������豸��ӵ��������б�����ͨ��BaseLanClient��̳�������
 * 			�Ķ����е�getLanDevices()���ص�ArrayList�����ȡ���Ѿ����ߵ������豸�����з��������ߣ������ڵ���֮ǰ������ɾ�����豸�б��еĶ���
	 * @param listSize
	 * @param device
	 * @param state
	 */
	public void onServiceDeviceStateChanged(int listSize,ServiceDevice device,byte state);
}
