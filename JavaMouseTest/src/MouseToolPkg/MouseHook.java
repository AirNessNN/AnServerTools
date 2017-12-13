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
	// ����¼�����
	public static final int WM_MOUSEMOVE = 512;//�ƶ�
	public static final int WM_LBUTTONDOWN = 513;//�������
	public static final int WM_LBUTTONUP = 514;//����ſ�
	public static final int WM_RBUTTONDOWN = 516;//�Ҽ�����
	public static final int WM_RBUTTONUP = 517;//�Ҽ��ſ�
	public static final int WM_MBUTTONDOWN = 519;//�м�����
	public static final int WM_MBUTTONUP = 520;//�м��ſ�
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

	// ��ӹ��Ӽ���
	public void addMouseHookListener(MouseHookListener mouseHook) {
		this.mouseHook = mouseHook;
		this.mouseHook.lib = lib;
	}

	// ����
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

	// �ر�
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
				// �ص�����
				public LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam) {
					if (nCode >= 0) {
						switch (wParam.intValue()) {
						case MouseHook.WM_LBUTTONDOWN:
							System.err.println("�������");
							break;
						case MouseHook.WM_LBUTTONUP:
							System.err.println("����ͷ�");
							break;
						case MouseHook.WM_MBUTTONDOWN:
							break;
						case MouseHook.WM_MBUTTONUP:
							break;
						case MouseHook.WM_MOUSEWHEEL:
							System.out.println("����1");
							break;
						}
						System.out.println(lParam.pt.x + " y=" + lParam.pt.y);
					}
					// ��������Ϣ���ݵ���ǰ�������е���һ���ӳ̣�һ�����ӳ�����Ե����������֮ǰ��֮��������Ϣ
					// hhk����ǰ���ӵľ��
					// nCode �����Ӵ���;
					// ���Ǹ���һ������Ҫ�����ģ������ݸ���ǰHook���̵Ĵ��롣��һ�����ӳ���ʹ�ô˴��룬��ȷ����δ�������Ϣ��
					// wParam��Ҫ���ݵĲ���; �ɹ������;�����ʲô�������˲����ĺ���ȡ���ڵ�ǰ�Ĺ����빳�����͡�
					// lParam��Param��ֵ���ݸ���ǰHook���̡��˲����ĺ���ȡ���ڵ�ǰ�Ĺ����빳�����͡�
					return lib.CallNextHookEx(hhk, nCode, wParam, lParam.getPointer());
				}
			});
			mouseHook.startWindowsHookEx();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
