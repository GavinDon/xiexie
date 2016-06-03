package com.lhdz.dataUtil;

import java.io.Serializable;
import java.util.Arrays;

import android.annotation.SuppressLint;

import com.lhdz.util.UniversalUtils;


public class HandleNetHeadMsg implements Serializable{
	
	// 企业标识
	// final static int EnterpriseId = 0;
	public char ucEnterpriseId;
	// 业务类型标识，用来指明自己是何种业务.
	// final static int BussinessID = 1;
	public char uBussinessID;
	// 加密标识 0:不加密，1：--目前所有消息默认为不加密,使用结构体，但加密标示保留
	// final static int EncryptType = 2;
	public char ucEncryptType;
	// 消息类型
	// final static int MsgType = 3;
	public int uiMsgType;
	// 客户端类型
	// final static int ClientType = 7;
	public int uiClientType;
	// 消息流水号
	// final static int SequenceNo = 11;
	public int uiSequenceNo;
	// 消息体实际长度(序列话后的)
	// final static int MsgRealLen = 15;
	public int uiMsgRealLen;
	// 加密后消息体长度
	// final static int MsgLen = 19;
	public int uiMsgLen;

	// final static int iLength = 23;
	// byte[] head;
	

	
	/**
	 * 构建消息头
	 * @param uiMsgType 消息类型
	 * @param uiMsgRealLen 消息实际长度（序列化后的）
	 * @param uiMsgLen 消息长度 （ 加密后的）
	 * @param ucEncryptType 是否加密
	 * @return
	 */
	public static byte[]  buildHeadMsg(int uiMsgType , int uiMsgRealLen , int uiMsgLen , int uiSequenceNo, int ucEncryptType){
		
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		
//		head = new byte[iLength];
//		head[EnterpriseId] = (byte)0xAA;
//		head[BussinessID] = (byte)11;
//		head[EncryptType] = (byte) ucEncryptType;
//		SetMsgType(uiMsgType);
//		SetClientType(0);
//		SetSequenceNo(0);
//		SetMsgRealLen(uiMsgRealLen);
//		SetMsgLen(uiMsgLen);
		
		headMsg.ucEnterpriseId = 170;
		headMsg.uBussinessID = 11;
		headMsg.ucEncryptType = (char) ucEncryptType;
		headMsg.uiMsgType = uiMsgType;
		headMsg.uiClientType = 0;
		headMsg.uiSequenceNo = uiSequenceNo;
		headMsg.uiMsgRealLen = uiMsgRealLen;
		headMsg.uiMsgLen = uiMsgLen;
		
		
		byte[] ucEnterpriseIdArray = UniversalUtils.charToByte_Head(headMsg.ucEnterpriseId);
		byte[] uBussinessIDArray = UniversalUtils.charToByte_Head(headMsg.uBussinessID);
		byte[] ucEncryptTypeArray = UniversalUtils.charToByte_Head(headMsg.ucEncryptType);
		byte[] uiMsgTypeArray = UniversalUtils.intToBytes_Head(headMsg.uiMsgType);
		byte[] uiClientTypeArray = UniversalUtils.intToBytes_Head(headMsg.uiClientType);
		byte[] uiSequenceNoArray = UniversalUtils.intToBytes_Head(headMsg.uiSequenceNo);
		byte[] uiMsgRealLenArray = UniversalUtils.intToBytes_Head(headMsg.uiMsgRealLen);
		byte[] uiMsgLenArray = UniversalUtils.intToBytes_Head(headMsg.uiMsgLen);
		
		byte[] headMsgArray1 = UniversalUtils.copyByteArray(ucEnterpriseIdArray, uBussinessIDArray);
		byte[] headMsgArray2 = UniversalUtils.copyByteArray(headMsgArray1, ucEncryptTypeArray);
		byte[] headMsgArray3 = UniversalUtils.copyByteArray(headMsgArray2, uiMsgTypeArray);
		byte[] headMsgArray4 = UniversalUtils.copyByteArray(headMsgArray3, uiClientTypeArray);
		byte[] headMsgArray5 = UniversalUtils.copyByteArray(headMsgArray4, uiSequenceNoArray);
		byte[] headMsgArray6 = UniversalUtils.copyByteArray(headMsgArray5, uiMsgRealLenArray);
		byte[] headMsgArray7 = UniversalUtils.copyByteArray(headMsgArray6, uiMsgLenArray);
		
		return headMsgArray7;
	}

	
	/**
	 * 解析消息头
	 * @param proBuf
	 * @return
	 */
	@SuppressLint("NewApi")
	public static HandleNetHeadMsg parseHeadMag(byte[] proBufHead){
		
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		
		headMsg.ucEnterpriseId = (char) proBufHead[0];
		headMsg.uBussinessID = (char) proBufHead[1];
		headMsg.ucEncryptType = (char) proBufHead[2];
		headMsg.uiMsgType = UniversalUtils.byteToInt(Arrays.copyOfRange(proBufHead, 3, 7));
		headMsg.uiClientType = UniversalUtils.byteToInt(Arrays.copyOfRange(proBufHead, 7, 11));
		headMsg.uiSequenceNo = UniversalUtils.byteToInt(Arrays.copyOfRange(proBufHead, 11, 15));
		headMsg.uiMsgRealLen = UniversalUtils.byteToInt(Arrays.copyOfRange(proBufHead, 15, 19));
		headMsg.uiMsgLen = UniversalUtils.byteToInt(Arrays.copyOfRange(proBufHead, 19, 23));
		
		return headMsg;
	}
	
	
	
//	public void SetMsgType(int mType)
//	{
////		head = intToBytes2(mType);
//		head[MsgType] = (byte)mType;
//		head[MsgType+1] = (byte)(mType >> 8 & 0xFF);
//		head[MsgType+2] = (byte)(mType >> 16 & 0xFF);
//		head[MsgType+3] = (byte)(mType >>24 & 0xFF);
//	}
//	public void SetClientType(int cType)
//	{
////		head = intToBytes2(cType);
//		head[ClientType] = (byte)cType;
//		head[ClientType+1] = (byte)(cType >> 8 & 0xFF);
//		head[ClientType+2] = (byte)(cType >> 16 & 0xFF);
//		head[ClientType+3] = (byte)(cType >>24 & 0xFF);
//	}
//	public void SetSequenceNo(int seq)
//	{
////		head = intToBytes2(seq);
//		head[SequenceNo] = (byte)seq;
//		head[SequenceNo+1] = (byte)(seq >> 8 & 0xFF);
//		head[SequenceNo+2] = (byte)(seq >> 16 & 0xFF);
//		head[SequenceNo+3] = (byte)(seq >>24 & 0xFF);
//	}
//	public void SetMsgRealLen(int mreal)
//	{
////		head = intToBytes2(mreal);
//		head[MsgRealLen] = (byte)mreal;
//		head[MsgRealLen+1] = (byte)(mreal >> 8 & 0xFF);
//		head[MsgRealLen+2] = (byte)(mreal >> 16 & 0xFF);
//		head[MsgRealLen+3] = (byte)(mreal >>24 & 0xFF);
//	}
//	public void SetMsgLen(int mlen)
//	{
////		head = intToBytes2(mlen);
//		head[MsgLen] = (byte)mlen;
//		head[MsgLen+1] = (byte)(mlen >> 8 & 0xFF);
//		head[MsgLen+2] = (byte)(mlen >> 16 & 0xFF);
//		head[MsgLen+3] = (byte)(mlen >>24 & 0xFF);
//	}
	
	
}
