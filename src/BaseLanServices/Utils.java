package BaseLanServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * An��BaseLAN�����࣬�ṩ�������л��������л��������豸ʵ��������
 * @author AN
 *
 */
public class Utils {
	/**
	 * �������л�
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
	 * �������л�
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
	  * �����UDP���ݷ�װ��
	  * @param data ����������
	  * @param packetLen �����ܳ���
	  * @param dataLen ���Գ���
	  * @return ����serviceDeviceʵ��
	  */
	 public static ServiceDevice getServiceDevice(byte[] data,int packetLen,final int dataLen) {
		 int index=packetLen-dataLen;
		byte[] userData=null;
		byte stateB=0;//״̬��0�Ƿ����������У�1�Ƿ��񼴽��ر�
		String tmpIP=null;
		
		
		ServiceDevice device=null;
		
		stateB=data[index];
		tmpIP=new String(data, index+1,dataLen-1);
		if(!isRightHostAddress(tmpIP)){
			//System.out.println("ʶ��ʧ��");
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
	  * ���IP��ַ��ȷ��
	  * @param rex Ҫ����IP
	  * @return
	  */
	 public static boolean isRightHostAddress(String rex) {
		 Pattern pat = Pattern.compile(
					"^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
							+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");// ������ʽƥ��IP��ַ
			Matcher matcher = null;
			matcher=pat.matcher(rex);
			if(matcher.matches()){
				return true;
			}
		return false;
	}

}
