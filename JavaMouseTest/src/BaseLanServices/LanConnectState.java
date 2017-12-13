package BaseLanServices;

/**
 * BaseLan的枚举型常量，代表各种连接中出现的状态
 * @author AN
 *
 */
public enum LanConnectState {
	/**
	 * 已经连接上目标
	 */
	CONNECTED,
	/**
	 * 断开连接状态
	 */
	DISCONNECTED,
	/**
	 *	未知状态
	 */
	UNKNOW,
	/**
	 * 未连接且发生错误
	 */
	ERROR,
	/**
	 * 已经连接上目标且数据收到
	 */
	DATA_ACCEPTED,
	/**
	 * 已经连接上目标且数据发送
	 */
	DATA_SENDED,
	/**
	 * 已经连接上目标但数据损坏
	 */
	DATA_ERROR
}
