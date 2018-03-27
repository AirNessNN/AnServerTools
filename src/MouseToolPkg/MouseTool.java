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
 * 鼠标工具类，封装录制和播放功能，实现了鼠标全局监控<br/>
 * 鼠标底层控制，等一系列高级功能
 * @author AN
 *
 */
public final class MouseTool {
	//鼠标当前的坐标
	private Point mouseLocation=null;
	//鼠标录制线程
	private Thread transcribeThread=null;
	//线程停止标记
	private volatile boolean threadFlag=false;
	private boolean mouseInfoThreadFlag=false;
	//鼠标轨迹数组
	private MouseNode[] mouseTrajectory;
	//FPS
	private long FPS=16;
	//当前鼠标是否有点击操作
	//private boolean isMousePress;
	//录制步数
	private int step=0;
	
	//private final static int MAXAR=214748364;
	
	private Thread thread=null;
	/**
	 * 获得当前鼠标轨迹的步数
	 * @return
	 */
	public long getStep(){
		return step;
	}
	//机器人类
	private Robot robot;
	
	private MouseHook mouseHook;//鼠标钩子
	
	//鼠标动作
	private boolean LButtonState=false;
	private boolean RButtonState=false;
	private boolean MButtonState=false;
	HWND dData;
	//private int WheelState=0;
	
	//录制完成回调
	private MouseTranscribeFinishedCallback transcribeFinishedCallback=null;
	
	
	
	
	/**
	 * 实例化一个鼠标工具类
	 */
	public MouseTool() {
		// TODO Auto-generated constructor stub
		try {
			robot=new Robot();//实例化机器人
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 打开鼠标位置监听线程，在启动录制之前一定要打开鼠标监听线程
	 * @param I 回调的方法
	 */
	public void startMouseInfoThread(MouseThreadListener I){
		//鼠标坐标线程
		if(mouseInfoThreadFlag){
			return;
		}
		//挂载钩子
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
		//异步启动钩子
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
					I.mouseMovePerformed(point.x, point.y);//返回参数给用户使用
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
	 * 关闭鼠标位置监听线程
	 */
	public void stopMouseInofThread() {
		mouseInfoThreadFlag=false;
		if(mouseHook!=null) {
			mouseHook.stopWindowsHookEx();
		}
	}
	
	
	
	
	/**
	 * 录制键盘，鼠标轨迹和动作，在启动录制之前必须要启动鼠标监听线程
	 * @param mouseOperation 返回给用户的操作
	 */
	public boolean transcrible(MouseOperation mouseOperation) {
		if(!threadFlag&&mouseInfoThreadFlag){//准备启动
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
	 * 结束鼠标录制
	 * @param I 结束录制的回调，返回已经完成录制的轨迹List
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
	 * 结束录制，如果在此之前已经设置回调，则会自动返回录制的轨迹List
	 */
	public void finishTranscrible() {
		finishTranscrible(transcribeFinishedCallback);
	}
	
	
	/**
	 * 播放已经录制好的鼠标轨迹
	 * @param I 需要进行的操作
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
				 * 鼠标播放应该是：放开鼠标应该在鼠标移动之前放开，点击鼠标应该在移动之后点击
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
						//移动之前的释放
						robot.mouseMove(thisNode.x, thisNode.y);//移动鼠标
						//移动之后的点击
						if(thisNode.LButton!=lastNode.LButton&&thisNode.LButton) {
							robot.mousePress(InputEvent.BUTTON1_MASK);
						}
						if(thisNode.RButton!=lastNode.RButton&&thisNode.RButton) {
							robot.mousePress(InputEvent.BUTTON3_MASK);
						}
						if(thisNode.MButton!=lastNode.MButton&&thisNode.MButton) {
							robot.mousePress(InputEvent.BUTTON2_MASK);
						}
						//回调
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
	 * 播放非类录制的鼠标轨迹
	 * @param mouseTrajectory 想要播放的鼠标轨迹
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
					//移动之前的释放
					robot.mouseMove(thisNode.x, thisNode.y);//移动鼠标
					//移动之后的点击
					if(thisNode.LButton!=lastNode.LButton&&thisNode.LButton) {
						robot.mousePress(InputEvent.BUTTON1_MASK);
					}
					if(thisNode.RButton!=lastNode.RButton&&thisNode.RButton) {
						robot.mousePress(InputEvent.BUTTON3_MASK);
					}
					if(thisNode.MButton!=lastNode.MButton&&thisNode.MButton) {
						robot.mousePress(InputEvent.BUTTON2_MASK);
					}
					//回调
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
	 * 设置鼠标路径录制完成的回调
	 * @param I
	 */
	public void setTranscribeFinishedCallback(MouseTranscribeFinishedCallback I) {
		this.transcribeFinishedCallback=I;
	}
	
	
}

