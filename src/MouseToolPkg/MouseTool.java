package MouseToolPkg;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
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
	private Point mouseLocation=null;
	//���¼���߳�
	private Thread transcribeThread=null;
	//�߳�ֹͣ���
	private volatile boolean threadFlag=false;
	private boolean mouseInfoThreadFlag=false;
	//���켣����
	private MouseNode[] mouseTrajectory;
	//FPS
	private long FPS=16;
	//��ǰ����Ƿ��е������
	//private boolean isMousePress;
	//¼�Ʋ���
	private int step=0;
	
	//private final static int MAXAR=214748364;
	
	private Thread thread=null;
	/**
	 * ��õ�ǰ���켣�Ĳ���
	 * @return
	 */
	public long getStep(){
		return step;
	}
	//��������
	private Robot robot;
	
	private MouseHook mouseHook;//��깳��
	
	//��궯��
	private boolean LButtonState=false;
	private boolean RButtonState=false;
	private boolean MButtonState=false;
	HWND dData;
	//private int WheelState=0;
	
	//¼����ɻص�
	private MouseTranscribeFinishedCallback transcribeFinishedCallback=null;
	
	
	
	
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
	public void startMouseInfoThread(MouseThreadListener I){
		//��������߳�
		if(mouseInfoThreadFlag){
			return;
		}
		//���ع���
		if(mouseHook==null) {
			mouseHook=new MouseHook();
		}
		mouseHook.addMouseHookListener(new MouseHookListener() {
			
			@Override
			public LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam) {
				// TODO Auto-generated method stub
				if (nCode >= 0) {
					switch (wParam.intValue()) {
					case MouseAciton.WM_LBUTTON_DOWN:
						LButtonState=true;
						break;
					case MouseAciton.WM_LBUTTON_UP:
						LButtonState=false;
						break;
					case MouseAciton.WM_MBUTTON_DOWN:
						MButtonState=true;
						break;
					case MouseAciton.WM_MBUTTON_UP:
						MButtonState=false;
						break;
					case MouseAciton.WM_MOUSE_WHEEL:
						
						break;
					case MouseAciton.WM_RBUTTON_DOWN:
						RButtonState=true;
						break;
					case MouseAciton.WM_RBUTTON_UP:
						RButtonState=false;
						break;
					}
					if(mouseInfoThreadFlag) {
						I.mouseActionPerformed(wParam.intValue());
					}
				}
				return lib.CallNextHookEx(hhk, nCode, wParam, lParam.getPointer());
			}
		});
		//�첽��������
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mouseHook.startWindowsHookEx();
			}
		}).start();
		
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
		if(mouseHook!=null) {
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
				mouseTrajectory=new MouseNode[100000];
				transcribeThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(threadFlag){
							MouseNode node=new MouseNode(mouseLocation.x,mouseLocation.y,LButtonState,RButtonState,MButtonState);
							mouseTrajectory[step++]=node;
							mouseOperation.addOperation(step, mouseTrajectory);
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
	 * �������¼��
	 * @param I ����¼�ƵĻص��������Ѿ����¼�ƵĹ켣List
	 */
	public void finishTranscrible(MouseTranscribeFinishedCallback I) {
		if(threadFlag) {
			threadFlag=false;
			if(I!=null) {
				ArrayList<MouseNode> tmpNodes=new ArrayList<>();
				for(int i=0;i<step;i++) {
					tmpNodes.add(mouseTrajectory[i]);
				}
				I.finish(tmpNodes);
			}
		}
	}
	
	/**
	 * ����¼�ƣ�����ڴ�֮ǰ�Ѿ����ûص�������Զ�����¼�ƵĹ켣List
	 */
	public void finishTranscrible() {
		finishTranscrible(transcribeFinishedCallback);
	}
	
	
	/**
	 * �����Ѿ�¼�ƺõ����켣
	 * @param I ��Ҫ���еĲ���
	 */
	public void play(MouseOperation I) {
		if(threadFlag==true) {
			threadFlag=false;
			if(transcribeFinishedCallback!=null) {
				ArrayList<MouseNode> tmpNodes=new ArrayList<>();
				for(int i=0;i<step;i++) {
					tmpNodes.add(mouseTrajectory[i]);
				}
				transcribeFinishedCallback.finish(tmpNodes);
			}
		}
		
		thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				/*
				 * ��겥��Ӧ���ǣ��ſ����Ӧ��������ƶ�֮ǰ�ſ���������Ӧ�����ƶ�֮����
				 */
				synchronized (robot) {
					for(int i=0;i<step;i++) {
						MouseNode thisNode=mouseTrajectory[i];
						MouseNode lastNode=mouseTrajectory[i];
						if(i>0) {
							lastNode=mouseTrajectory[i-1];
						}
						if(thisNode.LButton!=lastNode.LButton&&!thisNode.LButton) {
							robot.mouseRelease(InputEvent.BUTTON1_MASK);
						}
						if(thisNode.RButton!=lastNode.RButton&&!thisNode.RButton) {
							robot.mouseRelease(InputEvent.BUTTON3_MASK);
						}
						if(thisNode.MButton!=lastNode.MButton&&!thisNode.MButton) {
							robot.mouseRelease(InputEvent.BUTTON2_MASK);
						}
						//�ƶ�֮ǰ���ͷ�
						robot.mouseMove(thisNode.x, thisNode.y);//�ƶ����
						//�ƶ�֮��ĵ��
						if(thisNode.LButton!=lastNode.LButton&&thisNode.LButton) {
							robot.mousePress(InputEvent.BUTTON1_MASK);
						}
						if(thisNode.RButton!=lastNode.RButton&&thisNode.RButton) {
							robot.mousePress(InputEvent.BUTTON3_MASK);
						}
						if(thisNode.MButton!=lastNode.MButton&&thisNode.MButton) {
							robot.mousePress(InputEvent.BUTTON2_MASK);
						}
						//�ص�
						I.addOperation(i, mouseTrajectory);
						try {
							Thread.sleep(FPS);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON2_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
				}
			}
		});
		thread.start();
	}
	
	
	
	
	
	/**
	 * ���ŷ���¼�Ƶ����켣
	 * @param mouseTrajectory ��Ҫ���ŵ����켣
	 */
	public void play(ArrayList<MouseNode>mouseTrajectory){
		if(threadFlag==true) {
			threadFlag=false;
			if(transcribeFinishedCallback!=null) {
				ArrayList<MouseNode> tmpNodes=new ArrayList<>();
				for(int i=0;i<step;i++) {
					tmpNodes.add(this.mouseTrajectory[i]);
				}
				transcribeFinishedCallback.finish(tmpNodes);
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0;i<mouseTrajectory.size();i++){
					MouseNode thisNode=mouseTrajectory.get(i);
					MouseNode lastNode=mouseTrajectory.get(i);
					if(i>0) {
						lastNode=mouseTrajectory.get(i-1);
					}
					if(thisNode.LButton!=lastNode.LButton&&!thisNode.LButton) {
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
					if(thisNode.RButton!=lastNode.RButton&&!thisNode.RButton) {
						robot.mouseRelease(InputEvent.BUTTON3_MASK);
					}
					if(thisNode.MButton!=lastNode.MButton&&!thisNode.MButton) {
						robot.mouseRelease(InputEvent.BUTTON2_MASK);
					}
					//�ƶ�֮ǰ���ͷ�
					robot.mouseMove(thisNode.x, thisNode.y);//�ƶ����
					//�ƶ�֮��ĵ��
					if(thisNode.LButton!=lastNode.LButton&&thisNode.LButton) {
						robot.mousePress(InputEvent.BUTTON1_MASK);
					}
					if(thisNode.RButton!=lastNode.RButton&&thisNode.RButton) {
						robot.mousePress(InputEvent.BUTTON3_MASK);
					}
					if(thisNode.MButton!=lastNode.MButton&&thisNode.MButton) {
						robot.mousePress(InputEvent.BUTTON2_MASK);
					}
					//�ص�
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
	 * �������·��¼����ɵĻص�
	 * @param I
	 */
	public void setTranscribeFinishedCallback(MouseTranscribeFinishedCallback I) {
		this.transcribeFinishedCallback=I;
	}
	
	
}

