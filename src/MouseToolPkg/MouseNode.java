package MouseToolPkg;

import java.awt.Point;
import java.io.Serializable;

public class MouseNode extends Point implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean LButton;
	public boolean RButton;
	public boolean MButton;
	
	public MouseNode(int x,int y,boolean lb,boolean rb,boolean mb) {
		// TODO Auto-generated constructor stub
		this.x=x;
		this.y=y;
		LButton=lb;
		RButton=rb;
		MButton=mb;
	}
	
}