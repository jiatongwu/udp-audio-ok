package cn.xvkang.udpaudio.udpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class UdpClient {
	public static void main(String[] args) throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		 InetAddress IPAddress = InetAddress.getByName("47.92.226.141");
		//InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
		byte[] sendData = new byte[1];

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		int i = 0;
		while (true) {

//			DatagramPacket sendPacket2 = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
//			clientSocket.send(sendPacket2);

			byte[] receiveData = new byte[50000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			byte[] array = receivePacket.getData();

			// 转换为输入流
			// 定义字节数组输入输出流
			ByteArrayInputStream bais = new ByteArrayInputStream(array);
			AudioFormat af2 = UdpHandler.getAudioFormat();
			// 定义音频输入流
			AudioInputStream ais = new AudioInputStream(bais, af2, array.length / af2.getFrameSize());

			try {
				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af2);
				// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
				SourceDataLine sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				sd.open(af2);
				sd.start();
				// 创建播放进程
				Thread t2 = new Thread(() -> {
					byte btstmp[] = new byte[50000];
					try {
						int cntTmp;
						// 读取数据到缓存数据
						while ((cntTmp = ais.read(btstmp, 0, btstmp.length)) != -1) {
							if (cntTmp > 0) {
								// 写入缓存数据
								// 将音频数据写入到混频器
								sd.write(btstmp, 0, cntTmp);
							}
						}
						// System.out.println("play thread stop");

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						sd.drain();
						sd.close();
					}
				});
				t2.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// 关闭流
					if (ais != null) {
						ais.close();
					}
					if (bais != null) {
						bais.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i++;
			if (i == 999999999) {
				i = 0;
			}

		}
	}

}
