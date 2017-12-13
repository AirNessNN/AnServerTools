package MouseToolPkg;
/**
 * 鼠标工具动作监听器
 * @author AN
 *
 */
public interface MouseThreadListener {
	/**
	 * 鼠标移动回调事件
	 * @param x 鼠标x坐标
	 * @param y 鼠标y坐标
	 */
	public void mouseMovePerformed(int x,int y);
	/**
	 * 鼠标动作回调事件
	 * @param action 鼠标动作类枚举 MouseAction
	 */
	public void mouseActionPerformed(int action);
}
