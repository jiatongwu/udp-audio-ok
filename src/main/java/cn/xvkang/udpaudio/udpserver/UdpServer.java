package cn.xvkang.udpaudio.udpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class UdpServer {

	public static void main(String[] args) throws IOException {
		ConcurrentHashMap<String, InetAddress> map = new ConcurrentHashMap<>();
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[50000];
		
		while (true) {
			System.out.println("read one package");
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] array = receivePacket.getData();
				String hostAddress = receivePacket.getAddress().getHostAddress();
				int port = receivePacket.getPort();
				String hostKey = hostAddress + ":" + port;
				map.put(hostKey, receivePacket.getAddress());
				
				Thread sendThread = new Thread(() -> {
					for (String tmpKey : map.keySet()) {
						try {
							if(array.length<=10) {
								continue;
							}
							System.out.println("send one package to tmpkey");
							InetAddress inetAddress = map.get(tmpKey);
							DatagramPacket sendPacket = new DatagramPacket(array, array.length, inetAddress,
									Integer.parseInt(tmpKey.split(":")[1]));
							serverSocket.send(sendPacket);
							//map.clear();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
				sendThread.start();
			} catch (Exception ee) {
				ee.printStackTrace();
			}

//  			// 转换为输入流
//  			// 定义字节数组输入输出流
//  			ByteArrayInputStream bais = new ByteArrayInputStream(array);
//  			AudioFormat af2 = UdpHandler.getAudioFormat();
//  			// 定义音频输入流
//  			AudioInputStream ais = new AudioInputStream(bais, af2, array.length / af2.getFrameSize());
//
//  			try {
//  				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af2);
//  				// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
//  				SourceDataLine sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//  				sd.open(af2);
//  				sd.start();
//  				// 创建播放进程
//  				Thread t2 = new Thread(() -> {
//  					byte btstmp[] = new byte[50000];
//  					try {
//  						int cntTmp;
//  						// 读取数据到缓存数据
//  						while ((cntTmp = ais.read(btstmp, 0, btstmp.length)) != -1) {
//  							if (cntTmp > 0) {
//  								// 写入缓存数据
//  								// 将音频数据写入到混频器
//  								sd.write(btstmp, 0, cntTmp);
//  							}
//  						}
//  						System.out.println("play thread stop");
//
//  					} catch (Exception e) {
//  						e.printStackTrace();
//  					} finally {
//  						sd.drain();
//  						sd.close();
//  					}
//  				});
//  				t2.start();
//  			} catch (Exception e) {
//  				e.printStackTrace();
//  			} finally {
//  				try {
//  					// 关闭流
//  					if (ais != null) {
//  						ais.close();
//  					}
//  					if (bais != null) {
//  						bais.close();
//  					}
//
//  				} catch (Exception e) {
//  					e.printStackTrace();
//  				}
//  			}

		}
	}
}
