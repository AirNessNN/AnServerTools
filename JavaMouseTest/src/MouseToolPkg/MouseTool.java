package MouseToolPkg;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;

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
	private 							Point 									mouseLocation=null;
	//鼠标录制线程
	private 							Thread 									transcribeThread=null;
	//线程停止标记
	private 							boolean 								threadFlag=false;
	private 							boolean 								mouseInfoThreadFlag=false;
	//鼠标轨迹数组
	private 							ArrayList<MouseNode> 		mouseTrajectory;
	//FPS
	private 							long 									FPS=16;
	//当前鼠标是否有点击操作
	private 							boolean 								isMousePress;
	//录制步数
	private 							long 									step=0;
	
	//机器人类
	private static 					Robot 									robot=null;
	
	private							MouseHook							mouseHook=null;
	
	
	
	/**
	 * 获得当前鼠标轨迹的步数
	 * @return
	 */
	public long getStep(){
		return step;
	}
	
	
	
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
	public void startMouseInfoThread(MouseMoveThreadListener I){
		//鼠标坐标线程
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
		if(mouseHook!=null){
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
				mouseTrajectory=new ArrayList<MouseNode>();
				transcribeThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(threadFlag){
							mouseTrajectory.add(new MouseNode(mouseLocation.x,mouseLocation.y));
							step++;
							mouseOperation.addOperation(step, mouseTrajectory);//用户的操作
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
	 * 播放已经录制好的鼠标轨迹
	 * @param I 需要进行的操作
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
					I.addOperation(++kase, mouseTrajectory);//用户的操作
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
	 * 播放非类录制的鼠标轨迹
	 * @param mouseTrajectory 想要播放的鼠标轨迹
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

