package BaseLanServices;

/**
 * BaseLan��ö���ͳ�����������������г��ֵ�״̬
 * @author AN
 *
 */
public enum LanConnectState {
	/**
	 * �Ѿ�������Ŀ��
	 */
	CONNECTED,
	/**
	 * �Ͽ�����״̬
	 */
	DISCONNECTED,
	/**
	 *	δ֪״̬
	 */
	UNKNOW,
	/**
	 * δ�����ҷ�������
	 */
	ERROR,
	/**
	 * �Ѿ�������Ŀ���������յ�
	 */
	DATA_ACCEPTED,
	/**
	 * �Ѿ�������Ŀ�������ݷ���
	 */
	DATA_SENDED,
	/**
	 * �Ѿ�������Ŀ�굫������
	 */
	DATA_ERROR
}
