//package com.lhdz.socketutil;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//
//import android.content.Intent;
//import android.util.Log;
//
//import com.lhdz.dataUtil.HandleMsgDistribute;
//import com.lhdz.dataUtil.HandleNetHeadMsg;
//import com.lhdz.dataUtil.protobuf.NetHouseMsgPro;
//import com.lhdz.publicMsg.MyApplication;
//import com.lhdz.publicMsg.NetHouseMsgType;
//import com.lhdz.service.HandleNetDataService;
//import com.lhdz.util.Define;
//import com.lhdz.util.LogUtils;
//import com.lhdz.util.UniversalUtils;
//
///**
// * socket通讯
// * 
// * @author Administrator
// * 
// */
//public class SocketConn {
//	private static Socket socket = null ;
//	private String ip;
//	private int port;
//	private boolean flagConn = false;
//	private boolean flag = true;
//	private boolean flagRecv = true;
//	private boolean flagSend = true;
//	private byte[] body;
//	private static CallBackMsg cb;
//	private static ArrayList<byte[]> msgList = new ArrayList<byte[]>();
//	// 锁
//	private static Object lock = new Object();
//	// 超时时间
//	private long recvTime, sendTime;
//	// 传输所用的真实时间
//	private long conntime = recvTime - sendTime;
//
//	Thread sconn;
//
//	public SocketConn(String ip, int port) {
//		this.ip = ip;
//		this.port = port;
//		sconn = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				if (socket != null) {
//					try {
//						try {
//							flagConn = false;
//							flagRecv = false;
//							flagSend = false;
////							socket.wait(1000);
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}						
//						socket.close();
//						socket = null;
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				// 建立连接
//				conn();
//				// 开启发送数据线程
//				sendMsg();
//				// 开启接收数据线程
//				recvMsg();
//				// 开启socket检测链路
//				// Timer timer = new Timer();
//				// timer.scheduleAtFixedRate(new TimerTask() {
//				// @Override
//				// public void run() {
//				// if (socket != null && conntime > 60 * 1000) {
//				// try {
//				// socket.close();
//				// } catch (IOException e) {
//				// e.printStackTrace();
//				// }
//				// conn();
//				// }
//				//
//				// }
//				// }, 3000, 5000);
//			}
//		});
//		sconn.start();
//	}
//
//	/**
//	 * 链接请求
//	 * 
//	 * @param ip
//	 * @return
//	 */
//	public void conn() {
//
//		try {
//			while (flag) {
//				socket = new Socket(ip, port);
//				if (socket != null && socket.isConnected()) {
//					flag = false;
//					socket.setKeepAlive(true);
//					socket.setSoTimeout(60 * 1000);
//					flagConn = true;
//					flagRecv = true;
//					flagSend = true;
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//			// try {
//			// socket.close();
//			// } catch (IOException e1) {
//			// e1.printStackTrace();
//			// }
//		}
//	}
//
//	/**
//	 * 发送数据
//	 * 
//	 * @param msg
//	 */
//	public void sendMsg() {
//		if (flagConn) {
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					byte[] reqData;
//					// 从socket中获取输出流;
//					OutputStream out = null;
//					DataOutputStream dos = null;
//					while (flagSend) {
//
//						if(socket == null || socket.isClosed()){
//							
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							continue;
//						}
//						
//						if (msgList.isEmpty()) {
//							try {
//								Thread.sleep(1000);
//								continue;
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						} else {
//							try {
//								out = socket.getOutputStream();
//								dos = new DataOutputStream(out);
//								// 计算消息长度
//								if(msgList.size() <= 0){
//									continue;
//								}
//								// 加锁
//								synchronized (lock) {
//									reqData = msgList.get(0);
//									msgList.remove(0);
//									// msgList.notifyAll();
//								}
//								// 解锁
//								if(reqData == null){
//									LogUtils.w("reqData is null");
//									continue;
//								}
//								int iLen = reqData.length + 4;
//								byte[] mlen = new byte[4];
//								mlen[3] = (byte) ((iLen >>> 24) & 0xFF);
//								mlen[2] = (byte) ((iLen >>> 16) & 0xFF);
//								mlen[1] = (byte) ((iLen >>> 8) & 0xFF);
//								mlen[0] = (byte) ((iLen >>> 0) & 0xFF);
//								// 拼接消息数据
//								byte[] message = new byte[iLen];
//								//拷贝消息总长度
//								System.arraycopy(mlen, 0, message, 0, 4);
//								//拷贝具体消息
//								System.arraycopy(reqData, 0, message, 4, reqData.length);
//								// 发送
//								dos.write(message);
//								dos.flush();
//								sendTime = System.currentTimeMillis();
//								// PushData.setMessage(null);
//							} catch (IOException e) {
//								e.printStackTrace();
//								try {
//									if(dos != null){
//										dos.close();
//									}
//									if(out != null){
//										out.close();
//									}
//								} catch (IOException e1) {
//									e1.printStackTrace();
//								}
//							}
//
//						}
//
//					}
//				}
//			}).start();
//		}
//	}
//
//	/**
//	 * 接收数据
//	 */
//	public void recvMsg() {
//		if (flagConn) {
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// 从socket中获取输入流;
//					InputStream in = null;
//					DataInputStream dis = null;
//					while (flagRecv) {
//						
//						try {
//							
//							if(socket == null || socket.isClosed()){
//								
//								try {
//									Thread.sleep(1000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//								continue;
//							}
//							
//							in = socket.getInputStream();
//							dis = new DataInputStream(in);
//
//							// 将输入流压缩进数组以供msg解析;
//							if (dis.available() <= 0) {
//								try {
//									body = DealSocketData.getNetData();
//									Thread.sleep(1000);
//									continue;
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//							int iBufSize = dis.available();
//							byte[] btContent = new byte[iBufSize];
////							// 获取前四个字节代表的长度
////
////							byte[] btMsgLen = new byte[4];
////							int iLen = dis.read(btMsgLen, 0, 4);
////							int iLen2 = UniversalUtils.byteToInt(btMsgLen);
//
////							if (iLen2 < iBufSize) {
////								continue;
////							}
////							if (iBufSize < 27) {
////								continue;
////							}
//							
//							// 去掉前四位代表消息长度的字节，获取消息内容
//							int iLen1 = dis.read(btContent, 0, iBufSize);
//							
//							DealSocketData.pushNetData(btContent);
//							body = DealSocketData.getNetData();
//							
//							
////							body = new byte[iLen1];
////
////							System.arraycopy(btContent, 4, body, 0, iLen1);
////							Log.i("body", body + "-------");
////							// 调用业务层存储数据方法
////							// PushData.pushtoList(body);
////							
////							byte[] proBufHead = UniversalUtils.getNetMsgHead(body);
////							HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
////							HandleMsgDistribute.getInstance().insertMsg(headMsg.uiSequenceNo, body);
//						
//							if(body != null){
////								cb.RecvMsgSuccess(body);
//								
//								
////								byte[] proBufHead = UniversalUtils.getNetMsgHead(body);
////								HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//								long recvTime = System.currentTimeMillis();
//								HandleMsgDistribute.getInstance().insertRecvMsg(recvTime, body);
////								sendRecvBroad(recvTime);// 接收到返回的消息后发送广播通知客户端;
//								sendRecvService(recvTime);
//							}
//
//							
//						} catch (IOException e) {
//							e.printStackTrace();
//							try {
//								dis.close();
//								in.close();
//							} catch (IOException e1) {
//								e1.printStackTrace();
//							}
//						}
//					}
//
//				}
//			}).start();
//		}
//	}
//
//	public static void callbackRecData(CallBackMsg cbmsg) {
//		cb = cbmsg;
//	}
//
//	// 压入数据
//	public static void pushtoList(byte[] msg) {
//		synchronized (lock) {
//			msgList.add(msg);
//		}
//	}
//
////	/**
////	 * 通知客户端已经接收到服务器返回来的信息;
////	 */
////	public void sendRecvBroad(long recvTime) {
////		Intent intent = new Intent();
////		intent.setAction(Define.BROAD_CAST_RECV_DATA_AUTH);
////		intent.putExtra(Define.BROAD_RECVTIME, recvTime);
////		MyApplication.context.sendBroadcast(intent);
////	}
//	/**
//	 * 通知客户端已经接收到服务器返回来的信息--使用服务
//	 */
//	public void sendRecvService(long recvTime) {
//		Intent intent = new Intent();
//		Intent serviceIntent = new Intent(MyApplication.context, HandleNetDataService.class);
//		serviceIntent.setAction(Define.BROAD_CAST_RECV_DATA_AUTH);
//		serviceIntent.putExtra(Define.BROAD_RECVTIME, recvTime);
//		MyApplication.context.startService(serviceIntent);
//	}
//}
