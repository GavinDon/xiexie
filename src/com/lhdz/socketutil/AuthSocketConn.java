package com.lhdz.socketutil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.content.Intent;
import android.util.Log;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetHeadMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.service.HandleNetDataService;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

/**
 * socket通讯
 * 
 * @author Administrator
 * 
 */
public class AuthSocketConn {
	private static Socket socket = null ;
	private String ip;
	private int port;
	private boolean flagConn = false;
	private boolean flag = true;
	private boolean flagRecv = true;
	private boolean flagSend = true;
	private byte[] body;
//	private static CallBackMsg cb;
	private static ArrayList<byte[]> msgList = new ArrayList<byte[]>();
	// 锁
	private static Object lock = new Object();
	// 超时时间
	private long recvTime, sendTime;
	// 传输所用的真实时间
	private long conntime = recvTime - sendTime;

	Thread sconn;

	public AuthSocketConn(String ip, int port) {
		this.ip = ip;
		this.port = port;
		sconn = new Thread(new Runnable() {

			@Override
			public void run() {
				if (socket != null) {
					try {
						try {
							flagConn = false;
							flagRecv = false;
							flagSend = false;
//							socket.wait(10);
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
						if(socket != null && (!socket.isClosed())){
							socket.close();
						}
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// 建立连接
				conn();
				// 开启发送数据线程
				sendMsg();
				// 开启接收数据线程
				recvMsg();
				// 开启socket检测链路
				// Timer timer = new Timer();
				// timer.scheduleAtFixedRate(new TimerTask() {
				// @Override
				// public void run() {
				// if (socket != null && conntime > 60 * 10) {
				// try {
				// socket.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// conn();
				// }
				//
				// }
				// }, 3000, 5000);
			}
		});
		sconn.start();
	}

	/**
	 * 链接请求
	 * 
	 * @param ip
	 * @return
	 */
	public void conn() {

		try {
			while (flag) {
				socket = new Socket(ip, port);
				LogUtils.w("创建登录认证服务器的socket-- ip = "+ ip+"/ port = " + port);
				if (socket != null && socket.isConnected()) {
					flag = false;
					socket.setKeepAlive(true);
					socket.setSoTimeout(60 * 10);
					flagConn = true;
					flagRecv = true;
					flagSend = true;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// try {
			// socket.close();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param msg
	 */
	public void sendMsg() {
		if (flagConn) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					byte[] reqData;
					// 从socket中获取输出流;
					OutputStream out = null;
					DataOutputStream dos = null;
					while (flagSend) {

						if(socket == null || socket.isClosed()){
							
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						}
						
						if (msgList.isEmpty()) {
							try {
								Thread.sleep(10);
								continue;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							try {
								out = socket.getOutputStream();
								dos = new DataOutputStream(out);
								
								// 加锁
								synchronized (lock) {
									if (msgList.size() <= 0) {
										continue;
									}
									reqData = msgList.get(0);
									msgList.remove(0);
									// msgList.notifyAll();
								}
								// 解锁
								if(reqData == null){
									LogUtils.w("reqData is null");
									continue;
								}
								int iLen = reqData.length + 4;
								byte[] mlen = new byte[4];
								mlen[3] = (byte) ((iLen >>> 24) & 0xFF);
								mlen[2] = (byte) ((iLen >>> 16) & 0xFF);
								mlen[1] = (byte) ((iLen >>> 8) & 0xFF);
								mlen[0] = (byte) ((iLen >>> 0) & 0xFF);
								// 拼接消息数据
								byte[] message = new byte[iLen];
								//拷贝消息总长度
								System.arraycopy(mlen, 0, message, 0, 4);
								//拷贝具体消息
								System.arraycopy(reqData, 0, message, 4, reqData.length);
								// 发送
								dos.write(message);
								dos.flush();
								sendTime = System.currentTimeMillis();
								// PushData.setMessage(null);
							} catch (IOException e) {
								e.printStackTrace();
								try {
									if(dos != null){
										dos.close();
									}
									if(out != null){
										out.close();
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}

						}

					}
				}
			}).start();
		}
	}

	/**
	 * 接收数据
	 */
	public void recvMsg() {
		if (flagConn) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 从socket中获取输入流;
					InputStream in = null;
					DataInputStream dis = null;
					while (flagRecv) {
						
						try {
							
							if(socket == null || socket.isClosed()){
								
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								continue;
							}
							
							in = socket.getInputStream();
							dis = new DataInputStream(in);

							// 将输入流压缩进数组以供msg解析;
							if (dis.available() <= 0) {
								try {
									
									Thread.sleep(2);
									
									body = DealAuthSocketData.getNetData();
									
									if(body != null){
										long recvTime = System.currentTimeMillis();
										LogUtils.d("auth socket收到数据(缓存)--接收时间="+recvTime+"");
										HandleMsgDistribute.getInstance().insertRecvMsg(recvTime, body);
										sendRecvService(recvTime);
									}
									
									Thread.sleep(10);
									continue;
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							int iBufSize = dis.available();
							byte[] btContent = new byte[iBufSize];
//							// 获取前四个字节代表的长度
//
//							byte[] btMsgLen = new byte[4];
//							int iLen = dis.read(btMsgLen, 0, 4);
//							int iLen2 = UniversalUtils.byteToInt(btMsgLen);

//							if (iLen2 < iBufSize) {
//								continue;
//							}
//							if (iBufSize < 27) {
//								continue;
//							}
							
							// 去掉前四位代表消息长度的字节，获取消息内容
							int iLen1 = dis.read(btContent, 0, iBufSize);
							
							DealAuthSocketData.pushNetData(btContent);
							body = DealAuthSocketData.getNetData();
							
							
//							body = new byte[iLen1];
//
//							System.arraycopy(btContent, 4, body, 0, iLen1);
//							Log.i("body", body + "-------");
//							// 调用业务层存储数据方法
//							// PushData.pushtoList(body);
//							
//							byte[] proBufHead = UniversalUtils.getNetMsgHead(body);
//							HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//							HandleMsgDistribute.getInstance().insertMsg(headMsg.uiSequenceNo, body);
						
							if(body != null){
//								cb.RecvMsgSuccess(body);
								
								
//								byte[] proBufHead = UniversalUtils.getNetMsgHead(body);
//								HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
								long recvTime = System.currentTimeMillis();
								LogUtils.d("auth socket收到数据--接收时间="+recvTime+"");
								HandleMsgDistribute.getInstance().insertRecvMsg(recvTime, body);
//								sendRecvBroad(recvTime);// 接收到返回的消息后发送广播通知客户端;
								sendRecvService(recvTime);
							}

						} catch (IOException e) {
							e.printStackTrace();
							try {
								if (dis != null) {
									dis.close();
								}
								if (in != null) {
									in.close();
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}

				}
			}).start();
		}
	}



	// 压入数据
	public static void pushtoList(byte[] msg) {
		if(MyApplication.connState == false){
//			ToastUtils.show(MyApplication.context, "网络连接错误，请检查网络连接", 0);
//			return false;
			return;
		}
		synchronized (lock) {
			msgList.add(msg);
		}
//		return true;
	}
	
	
	//清空数据
	public static void clearMsgList(){
		synchronized (lock) {
			msgList.clear();
		}
	}


	/**
	 * 通知客户端已经接收到服务器返回来的信息--使用服务
	 */
	public void sendRecvService(long recvTime) {
		Intent intent = new Intent();
		Intent serviceIntent = new Intent(MyApplication.context, HandleNetDataService.class);
		serviceIntent.setAction(Define.BROAD_CAST_RECV_DATA_AUTH);
		serviceIntent.putExtra(Define.BROAD_RECVTIME, recvTime);
		MyApplication.context.startService(serviceIntent);
	}
	
	
	
	/**
	 * 将该socket断开
	 */
	public void closeAuthSocket() {
		if (socket != null) {
//			boolean isClose = socket.isClosed();
//			boolean isConn = socket.isConnected();
			if ((!socket.isClosed()) && socket.isConnected()) {
				flagConn = false;
				flagRecv = false;
				flagSend = false;
				try {
					socket.close();
					socket = null;
					LogUtils.w("登录认证服务器断开");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 返回登录认证socket的连接状态
	 * @return
	 */
	public boolean isClose() {
		if (socket != null) {
//			boolean isClose = socket.isClosed();
//			boolean isConn = socket.isConnected();
			if (socket.isClosed()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	
}
