package MouseToolPkg;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.MSG;

public class MouseHook {
	// 鼠标事件编码
	public static final int WM_MOUSEMOVE = 512;//移动
	public static final int WM_LBUTTONDOWN = 513;//左键按下
	public static final int WM_LBUTTONUP = 514;//左键放开
	public static final int WM_RBUTTONDOWN = 516;//右键按下
	public static final int WM_RBUTTONUP = 517;//右键放开
	public static final int WM_MBUTTONDOWN = 519;//中键按下
	public static final int WM_MBUTTONUP = 520;//中键放开
	public static final int WM_MOUSEHWHEEL  = 526;  
    public static final int WM_MOUSEWHEEL   = 522; 
	public User32 lib;
	private static HHOOK hhk;
	private MouseHookListener mouseHook;
	private HMODULE hMod;
	private boolean isWindows = false;
	private boolean flag;

	public MouseHook() {
		isWindows = Platform.isWindows();
		if (isWindows) {
			lib = User32.INSTANCE;
			hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		}

	}

	// 添加钩子监听
	public void addMouseHookListener(MouseHookListener mouseHook) {
		this.mouseHook = mouseHook;
		this.mouseHook.lib = lib;
	}

	// 启动
	public void startWindowsHookEx() {
		if (isWindows) {
			lib.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, hMod, 0);
			int result;
			MSG msg = new MSG();
			flag=true;
			while ((result = lib.GetMessage(msg, null, 0, 0)) != 0&&flag) {
				if (result == -1) {
					System.err.println("error in get message");
					break;
				} else {
					System.err.println("got message");
					lib.TranslateMessage(msg);
					lib.DispatchMessage(msg);
				}
			}
		}

	}

	// 关闭
	public void stopWindowsHookEx() {
		if (isWindows) {
			lib.UnhookWindowsHookEx(hhk);
			flag=false;
		}

	}

	public static void main(String[] args) {
		try {
			MouseHook mouseHook = new MouseHook();
			mouseHook.addMouseHookListener(new MouseHookListener() {
				// 回调监听
				public LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam) {
					if (nCode >= 0) {
						switch (wParam.intValue()) {
						case MouseHook.WM_LBUTTONDOWN:
							System.err.println("左键按下");
							break;
						case MouseHook.WM_LBUTTONUP:
							System.err.println("左键释放");
							break;
						case MouseHook.WM_MBUTTONDOWN:
							break;
						case MouseHook.WM_MBUTTONUP:
							break;
						case MouseHook.WM_MOUSEWHEEL:
							System.out.println("滚轮1");
							break;
						}
						System.out.println(lParam.pt.x + " y=" + lParam.pt.y);
					}
					// 将钩子信息传递到当前钩子链中的下一个子程，一个钩子程序可以调用这个函数之前或之后处理钩子信息
					// hhk：当前钩子的句柄
					// nCode ：钩子代码;
					// 就是给下一个钩子要交待的，钩传递给当前Hook过程的代码。下一个钩子程序使用此代码，以确定如何处理钩的信息。
					// wParam：要传递的参数; 由钩子类型决定是什么参数，此参数的含义取决于当前的钩链与钩的类型。
					// lParam：Param的值传递给当前Hook过程。此参数的含义取决于当前的钩链与钩的类型。
					return lib.CallNextHookEx(hhk, nCode, wParam, lParam.getPointer());
				}
			});
			mouseHook.startWindowsHookEx();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
