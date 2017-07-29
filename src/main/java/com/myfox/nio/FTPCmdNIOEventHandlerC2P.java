package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.config.MsgText;
import com.myfox.ftpcmd.C2PFTPCMDEnum;
import com.myfox.ftpcmd.FTPCMDProxyHandler;
import com.myfox.nioprocess.NIOProcessor;

/**
 * ftp客户端 连接到服务器的 网络事件处理
 * 
 * @author zss
 */
public class FTPCmdNIOEventHandlerC2P extends AbstractFTPCommandNIOHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCmdNIOEventHandlerC2P.class);

	/**
	 * @param channel
	 * @param selectKey
	 * @param config
	 */
	public FTPCmdNIOEventHandlerC2P(SocketChannel channel, SelectionKey selectKey, FtpProxyChannelConfig config,
			NIOProcessor process) {
		super(channel, selectKey);
		this.ftpSession = new FTPSession();
		ftpSession.setC2pHandler(this);
		ftpSession.setServerIp(config.getRemortAddress());
		ftpSession.setServerPort(config.getRemortPort());
		ftpSession.setProcess(process);
		try {
			this.answerSocket(MsgText.msgConnect + FtpProxyChannelConfig.CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handFtpCmd(String cmd) throws IOException {
		int i = cmd.indexOf(' ');
		if (i != -1) {
			String key = cmd.substring(0, i);
			FTPCMDProxyHandler ftpCmd = C2PFTPCMDEnum.getCmdHandler(key);
			if (ftpCmd != null) {
				ftpCmd.exec(ftpSession, cmd);
				LOGGER.debug("C->P:{}", cmd);
			} else {
				rorwardCmd(cmd);
			}
		} else {
			rorwardCmd(cmd);
		}
	}

	private void rorwardCmd(String cmd) throws IOException {
		ftpSession.getP2sHandler().answerSocket(cmd + CRLF);
		LOGGER.debug("C->S:{}", cmd);
	}

	public void close() {
		super.close();
		if (ftpSession.getP2sHandler() != null) {
			ftpSession.getP2sHandler().close();
		}
	}

}
