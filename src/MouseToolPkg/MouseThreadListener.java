package MouseToolPkg;
/**
 * ��깤�߶���������
 * @author AN
 *
 */
public interface MouseThreadListener {
	/**
	 * ����ƶ��ص��¼�
	 * @param x ���x����
	 * @param y ���y����
	 */
	public void mouseMovePerformed(int x,int y);
	/**
	 * ��궯���ص��¼�
	 * @param action ��궯����ö�� MouseAction
	 */
	public void mouseActionPerformed(int action);
}
