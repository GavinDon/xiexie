package com.lhdz.dataUtil;

import java.util.HashMap;
import java.util.Map;


public class  HandleMsgDistribute {

	private static Map<Long, byte[]> receiveMsgMap = new HashMap<Long, byte[]>();
	private static Map<Long, Object> completeMsgMap = new HashMap<Long, Object>();
	
	private HandleMsgDistribute() {}
	
	private static HandleMsgDistribute msgDistribute = null;
	
	/**
	 * 单例，获取HandleMsgDistribute的对象
	 * @return
	 */
	public synchronized static HandleMsgDistribute getInstance(){
		if(msgDistribute == null){
			msgDistribute = new HandleMsgDistribute();
		}
		return msgDistribute;
	}
	
	
	//从网络接收到的数据
	
	
	/**
	 * 根据recvTime删除一条数据
	 * @param recvTime
	 * @return 
	 */
	public synchronized void deleteRecvMsg(long recvTime){
		receiveMsgMap.remove(recvTime);
	}
	
	
	/**
	 * 将消息内容放入map中
	 * @param recvTime
	 * @param object
	 */
	public synchronized void insertRecvMsg(long recvTime,byte[] bs ){
		receiveMsgMap.put(recvTime, bs);
	}
	
	
	/**
	 * 根据recvTime查出消息内容
	 * @param recvTime
	 * @return
	 */
	public synchronized byte[] queryRecvMsg(long recvTime){
		
		byte[] bs = receiveMsgMap.get(recvTime);
		//如果根据sequenceNo能够查到消息内容，则需要删除map中的该条记录
		if(bs != null){
			deleteRecvMsg(recvTime);
		}
		
		return bs;
	}
	
	
	
	//处理完成的数据
	
	/**
	 * 根据SequenceNo删除一条数据
	 * @param SequenceNo
	 * @return 
	 */
	public synchronized void deleteCompleteMsg(long recvTime){
		completeMsgMap.remove(recvTime);
	}
	
	
	/**
	 * 将消息内容放入map中
	 * @param SequenceNo
	 * @param object
	 */
	public synchronized void insertCompleteMsg(long recvTime,Object object ){
		completeMsgMap.put(recvTime, object);
	}
	
	
	/**
	 * 根据SequenceNo查出消息内容
	 * @param SequenceNo
	 * @return
	 */
	public synchronized Object queryCompleteMsg(long recvTime){
		
		Object obj = completeMsgMap.get(recvTime);
		//如果根据sequenceNo能够查到消息内容，则需要删除map中的该条记录
		if(obj != null){
			deleteCompleteMsg(recvTime);
		}
		
		return obj;
	}
	
	
}
