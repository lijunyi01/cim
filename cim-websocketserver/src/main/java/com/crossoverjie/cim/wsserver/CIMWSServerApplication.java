package com.crossoverjie.cim.wsserver;

import com.crossoverjie.cim.wsserver.config.AppConfiguration;
import com.crossoverjie.cim.wsserver.kit.RegistryZK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class CIMWSServerApplication implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(CIMWSServerApplication.class);

	@Autowired
	private AppConfiguration appConfiguration ;

	@Value("${server.port}")
	private int httpPort ;

	public static void main(String[] args) {
        SpringApplication.run(CIMWSServerApplication.class, args);
		LOGGER.info("启动 Server 成功");
	}

	@Override
	public void run(String... args) throws Exception {
		//获得本机IP; 这种方法只能处理简单网络情况（单一网卡）
//		String addr = InetAddress.getLocalHost().getHostAddress();
		String addr = getLocalHostLANAddress().getHostAddress();
		// 启动线程注册到zookeeper（报告自己的ip、tcp/http端口信息,由于是websocket，因此没有单独的tcp端口）
		Thread thread = new Thread(new RegistryZK(addr,0,httpPort));
		thread.setName("registry-zk");
		thread.start() ;
	}

	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// 遍历所有的网络接口
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// 在所有的接口下再遍历IP

				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址且地址段符合，就是它了
							if(inetAddr.getHostAddress().startsWith("192.168.168")) {
								return inetAddr;
							}else{
								continue;
							}
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// 如果没有发现 non-loopback地址.只能用最次选的方案
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}
}