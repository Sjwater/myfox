package com.myfox.ftpcmd;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.config.MsgText;
import com.myfox.nio.FTPSession;
/**
 * 登录的后的命令 实现 次抽象类
 * @author zss
 */
public abstract class FTPCMDLoggedPorxyHandler implements FTPCMDProxyHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCMDLoggedPorxyHandler.class);
	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
       if(!session.isLogin())
       {
    	   session.getC2pHandler().answerSocket(MsgText.msgNotLoggedIn + FtpProxyChannelConfig.CRLF);
       }else
       {
    	   nextExec(session,cmd);
       }
	}

	public abstract void nextExec(FTPSession session, String cmd) throws IOException ;
}
