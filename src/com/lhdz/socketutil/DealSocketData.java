//package com.lhdz.socketutil;
//
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import com.lhdz.util.UniversalUtils;
//
//public class DealSocketData {
//
//	
//	private static int iBufferSize = 1024*10;
//	private static byte[] btStorage = new byte[iBufferSize];
//	private int iFirstPosition = 0;
//	private static int iLastPosition = 0;
//	private static int iCount = 0;
//	
//	private static Lock lock = new ReentrantLock();
//	
//	private static CallBackMsg cb;
//	
////	private static HandleSocketData handleSocketData = new HandleSocketData();
//	
////	private HandleSocketData(int bufferSize) {
////		this.iBufferSize = bufferSize;
////		btStorage = new byte[this.iBufferSize];
////	}
//	
////	private HandleSocketData(){
////		
////	}
////	
////	public synchronized static  HandleSocketData getInstance(){
////		if(handleSocketData == null){
////			handleSocketData = new HandleSocketData();
////		}
////		return handleSocketData;
////	}
//	
//	
//	
//	/**
//	 * 将数据放入数组中
//	 * @param newData
//	 */
//	public static void pushNetData(byte[] newData) {
//		lock.lock();
//
//		try {
//			
//			if(newData.length <= 0){
//				return;
//			}
//			
//			// 若数组剩余大小小于待拷贝数组的大小，则直接返回
//			if (iBufferSize - iLastPosition < newData.length) {
//				return;
//			}
//
//			// 将新的数据拷贝到数组中，从数组的iLastPosition位置开始拷贝，拷贝newData.length长度
//			System.arraycopy(newData, 0, btStorage, iLastPosition,
//					newData.length);
//			// 现有数据的个数
//			iCount = iLastPosition + newData.length;
//			// 重新给iLastPosition确定位置，为新的数据的长度 + 原来数据的位置
//			iLastPosition = iLastPosition + newData.length;
//		} finally {
//			lock.unlock();
//		}
//
//	}
//	
//	
//	/**
//	 * 从数组中取出需要的数据
//	 */
//	public static byte[] getNetData(){
//		lock.lock();
//		
//		try{
//			
//			if(iCount < 4){
//				return null;
//			}
//			
//			byte[] btTemp = new byte[4];
//			System.arraycopy(btStorage, 0, btTemp, 0, btTemp.length);
//			int iPackLength = UniversalUtils.byteToInt(btTemp);
//			
//			if(iPackLength <= 0 || iPackLength > iCount){
//				return null;
//			}
//			
//			byte[] btMsgData = new byte[iPackLength-4];
//			System.arraycopy(btStorage, 4, btMsgData, 0, iPackLength-4);
//			
//			iCount = iCount - iPackLength;
//			
//			if(iCount>0){
//				byte[] btRemain = new byte[iCount];
//				System.arraycopy(btStorage, iPackLength, btRemain, 0, iCount);
//				
//				System.arraycopy(btRemain, 0, btStorage, 0, iCount);
//				
//			}
//			iLastPosition = iCount;
//			
//			
//			
////			cb.RecvMsgSuccess(btMsgData);
//			return btMsgData;
//		}finally{
//			lock.unlock();
//		}
//		
//		
//	}
//	
//	public static void callbackRecData(CallBackMsg cbmsg) {
//		cb = cbmsg;
//	}
//	
//}
