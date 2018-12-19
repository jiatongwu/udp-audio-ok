package cn.xvkang.udpaudio.udpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class MinaUdpServer {

	private ConcurrentHashMap<SocketAddress, String> clients;

	public MinaUdpServer() throws IOException {
		clients = new ConcurrentHashMap<SocketAddress, String>();
		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(new UdpHandler(this));
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
		acceptor.bind(new InetSocketAddress(9999));
	}

	public static void main(String[] args) throws IOException {
		new MinaUdpServer();
		

	}

	public ConcurrentHashMap<SocketAddress, String> getClients() {
		return clients;
	}

	public void setClients(ConcurrentHashMap<SocketAddress, String> clients) {
		this.clients = clients;
	}

}
