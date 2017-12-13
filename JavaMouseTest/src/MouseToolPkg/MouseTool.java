package MouseToolPkg;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;

import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
/**
 * ��깤���࣬��װ¼�ƺͲ��Ź��ܣ�ʵ�������ȫ�ּ��<br/>
 * ���ײ���ƣ���һϵ�и߼�����
 * @author AN
 *
 */
public final class MouseTool {
	//��굱ǰ������
	private 							Point 									mouseLocation=null;
	//���¼���߳�
	private 							Thread 									transcribeThread=null;
	//�߳�ֹͣ���
	private 							boolean 								threadFlag=false;
	private 							boolean 								mouseInfoThreadFlag=false;
	//���켣����
	private 							ArrayList<MouseNode> 		mouseTrajectory;
	//FPS
	private 							long 									FPS=16;
	//��ǰ����Ƿ��е������
	private 							boolean 								isMousePress;
	//¼�Ʋ���
	private 							long 									step=0;
	
	//��������
	private static 					Robot 									robot=null;
	
	private							MouseHook							mouseHook=null;
	
	
	
	/**
	 * ��õ�ǰ���켣�Ĳ���
	 * @return
	 */
	public long getStep(){
		return step;
	}
	
	
	
	/**
	 * ʵ����һ����깤����
	 */
	public MouseTool() {
		// TODO Auto-generated constructor stub
		try {
			robot=new Robot();//ʵ����������
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * �����λ�ü����̣߳�������¼��֮ǰһ��Ҫ���������߳�
	 * @param I �ص��ķ���
	 */
	public void startMouseInfoThread(MouseMoveThreadListener I){
		//��������߳�
		if(mouseInfoThreadFlag){
			return;
		}
		if(mouseHook==null){
			mouseHook=new MouseHook();
		}
		mouseHook.addMouseHookListener(new MouseHookListener() {
			
			@Override
			public LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam) {
				// TODO Auto-generated method stub
				if(nCode>0){
					switch (wParam.intValue()) {
					case MouseHook.WM_LBUTTONDOWN:
						
						break;
					case MouseHook.WM_LBUTTONUP:
						
						break;
					case MouseHook.WM_MBUTTONDOWN:
						break;
					case MouseHook.WM_MBUTTONUP:
						
						break;
					case MouseHook.WM_RBUTTONDOWN:
						
						break;
					case MouseHook.WM_RBUTTONUP:
						
						break;
					case MouseHook.WM_MOUSEHWHEEL:
						
						break;
					}
				}
				return lib.CallNextHookEx(hhk, nCode, wParam, IP);
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mouseInfoThreadFlag=true;
				while(mouseInfoThreadFlag){
					Point point=MouseInfo.getPointerInfo().getLocation();
					mouseLocation=point;
					I.mouseMovePerformed(point.x, point.y);//���ز������û�ʹ��
					try{
						Thread.sleep(FPS);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	
	
	/**
	 * �ر����λ�ü����߳�
	 */
	public void stopMouseInofThread() {
		mouseInfoThreadFlag=false;
		if(mouseHook!=null){
			mouseHook.stopWindowsHookEx();
		}
	}
	
	
	
	
	/**
	 * ¼�Ƽ��̣����켣�Ͷ�����������¼��֮ǰ����Ҫ�����������߳�
	 * @param mouseOperation ���ظ��û��Ĳ���
	 */
	public boolean transcrible(MouseOperation mouseOperation) {
		if(!threadFlag&&mouseInfoThreadFlag){//׼������
			if(transcribeThread==null){
				threadFlag=true;
				step=0;
				mouseTrajectory=new ArrayList<MouseNode>();
				transcribeThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(threadFlag){
							mouseTrajectory.add(new MouseNode(mouseLocation.x,mouseLocation.y));
							step++;
							mouseOperation.addOperation(step, mouseTrajectory);//�û��Ĳ���
							try {
								Thread.sleep(FPS);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(!threadFlag){
							transcribeThread=null;
						}
					}
				});
				transcribeThread.start();
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	/**
	 * �����Ѿ�¼�ƺõ����켣
	 * @param I ��Ҫ���еĲ���
	 */
	public void play(MouseOperation I) {
		threadFlag=false;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int kase=0;
				for(MouseNode node :mouseTrajectory){
					robot.mouseMove(node.x, node.y);
					I.addOperation(++kase, mouseTrajectory);//�û��Ĳ���
					try {
						Thread.sleep(FPS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	
	
	
	/**
	 * ���ŷ���¼�Ƶ����켣
	 * @param mouseTrajectory ��Ҫ���ŵ����켣
	 */
	public void play(ArrayList<MouseNode>mouseTrajectory){
		new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(MouseNode node :mouseTrajectory){
							robot.mouseMove(node.x, node.y);
							try {
								Thread.sleep(FPS);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).start();
	}
}

