package MouseToolPkg;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;

public class MouseHookStruct extends Structure{
	public static class ByReference extends MouseHookStruct implements Structure.ByReference {};  
    public POINT pt; //������  
    public HWND hwnd;//���ھ��  
    public int wHitTestCode;  
    public ULONG_PTR dwExtraInfo; //��չ��Ϣ  
     
    //��������˳��  
 @Override  
 protected List getFieldOrder() {  
  return Arrays.asList("dwExtraInfo","hwnd","pt","wHitTestCode");  
 }  

}
