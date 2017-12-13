package MouseToolPkg;

import java.util.ArrayList;

/**
 * MouseTool类的动作附加操作接口
 * @author AN
 *
 */
public interface MouseOperation{
	public void addOperation(long step,ArrayList<MouseNode> mouseTrajectory);
}