package MouseToolPkg;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;

abstract class MouseHookListener implements HOOKPROC {
	public User32 lib = null; //windowӦ�ó���ӿ�  
	public HHOOK hhk; //���ӵľ��  
	 //�ص�  
	 //�������ֵ���е���һ�����ӳ��򣬷���ֵ�ĺ���ȡ���ڹ���  
	public abstract LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam);  
}
