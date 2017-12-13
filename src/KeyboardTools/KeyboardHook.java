package KeyboardTools;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
/**
 * 键盘监听类，利用Hook函数实现对全局的键盘事件监听
 * @author AN
 *
 */
public class KeyboardHook implements Runnable{

	private static HHOOK hook;
	private static LowLevelKeyboardProc keyboardProc;
	final static User32 lib=User32.INSTANCE;
	boolean flag;
	
	private KeyboardListeners I;
	
	
	/**
	 * 打开监听器，不可多次打开，需要实现KeyBoardListener方法监听
	 * @param I
	 */
	public void startKeyboardListening(KeyboardListeners I) {
		if(I!=null) {
			this.I=I;
		}
		if(flag) {
			return;
		}
		flag=true;
		new Thread(this).start();
	}
	
	/**
	 * 关闭之后将不会再回调KeyboardListener，需要重新打开
	 */
	public void stop() {
		flag=false;
		lib.UnhookWindowsHookEx(hook);
	}
	
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HMODULE hMod=Kernel32.INSTANCE.GetModuleHandle(null);
		keyboardProc=new LowLevelKeyboardProc() {
			
			@Override
			public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
				// TODO Auto-generated method stub
				int w=wParam.intValue();
				boolean b=false;
				if(w==WinUser.WM_KEYDOWN||w==WinUser.WM_SYSKEYDOWN) {
					b=true;
				}
				if(w==WinUser.WM_KEYUP||w==WinUser.WM_SYSKEYUP) {
					b=false;
				}
				if(I!=null&&flag) {
					I.actionPerformed(info.vkCode,b);
				}
				
				if(flag){
					return lib.CallNextHookEx(hook, nCode, wParam, info.getPointer());
				}else{
					return null;
					
				}
			}
		};
		hook=lib.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardProc, hMod, 0);
		int resule;
		MSG msg=new MSG();
		while((resule=lib.GetMessage(msg, null, 0, 0))!=0) {
			if(resule==-1) {
				
			}else {
				lib.TranslateMessage(msg);
				lib.DispatchMessage(msg);
			}
		}
		lib.UnhookWindowsHookEx(hook);
	}

}
