package BaseLanServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * An的BaseLAN工具类，提供对象序列化、反序列化、服务设备实例化工具
 * @author AN
 *
 */
public class Utils {
	/**
	 * 对象反序列化
	 * @param bytes
	 * @return
	 */
	public static Object toObject (byte[] bytes) {      
        Object obj = null;      
        try {        
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);        
            ObjectInputStream ois = new ObjectInputStream (bis);        
            obj = ois.readObject();      
            ois.close();   
            bis.close();   
        } catch (IOException ex) {        
            ex.printStackTrace();   
        } catch (ClassNotFoundException ex) {        
            ex.printStackTrace();   
        }      
        return obj;    
    }   
	
	
	/**
	 * 对象序列化
	 * @param obj
	 * @return
	 */
	 public static  byte[] toByteArray (Object obj) {      
	        byte[] bytes = null;      
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();      
	        try {        
	            ObjectOutputStream oos = new ObjectOutputStream(bos);         
	            oos.writeObject(obj);        
	            oos.flush();         
	            bytes = bos.toByteArray ();      
	            oos.close();         
	            bos.close();        
	        } catch (IOException ex) {        
	            ex.printStackTrace();   
	        }      
	        return bytes;    
	    }   
	 
	 /**
	  * 服务端UDP数据封装器
	  * @param data 服务器属性
	  * @param packetLen 数据总长度
	  * @param dataLen 属性长度
	  * @return 返回serviceDevice实例
	  */
	 public static ServiceDevice getServiceDevice(byte[] data,int packetLen,final int dataLen) {
		 int index=packetLen-dataLen;
		byte[] userData=null;
		byte stateB=0;//状态：0是服务正在运行，1是服务即将关闭
		String tmpIP=null;
		
		
		ServiceDevice device=null;
		
		stateB=data[index];
		tmpIP=new String(data, index+1,dataLen-1);
		if(!isRightHostAddress(tmpIP)){
			//System.out.println("识别失败");
			return null;
		}
		if(index>0){
			userData=new byte[index];
			for(int i=0;i<index;i++){
				userData[i]=data[i];
			}
		}
		device=new ServiceDevice(userData, tmpIP.getBytes(), stateB);
		return device;
	}
	 
	 /**
	  * 检测IP地址正确性
	  * @param rex 要检测的IP
	  * @return
	  */
	 public static boolean isRightHostAddress(String rex) {
		 Pattern pat = Pattern.compile(
					"^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
							+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");// 正则表达式匹配IP地址
			Matcher matcher = null;
			matcher=pat.matcher(rex);
			if(matcher.matches()){
				return true;
			}
		return false;
	}

}
