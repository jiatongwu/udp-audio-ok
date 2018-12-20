package cn.xvkang.udpaudio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import cn.xvkang.udpaudio.udpserver.MinaUdpClient;

public class AudioRecord {

	public static void main(String[] args) throws LineUnavailableException, SocketException {

		MinaUdpClient udpClient = new MinaUdpClient();

		AudioFormat af = getAudioFormat();

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
		// 定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
		TargetDataLine td = (TargetDataLine) (AudioSystem.getLine(info));
		// 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
		td.open(af);
		// 允许某一数据行执行数据 I/O
		td.start();
		 DatagramSocket clientSocket = new DatagramSocket();
		Thread t1 = new Thread(() -> {
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 定义存放录音的字节数组,作为缓冲区
			byte bts[] = new byte[20000];
			try {
				System.out.println("ok3");
				// stopflag = false;
				while (true) {
					// while (stopflag != true) {
					// 当停止录音没按下时，该线程一直执行
					// 从数据行的输入缓冲区读取音频数据。
					// 要读取bts.length长度的字节,cnt 是实际读取的字节数
					int cnt = td.read(bts, 0, bts.length);
					if (cnt > 0) {
						td.flush();
						// baos.write(bts, 0, cnt);
						// 通过udp发送出去
//						Thread t2 = new Thread(() -> {
//							udpClient.getSession().write(IoBuffer.wrap(Arrays.copyOf(bts, cnt)));
//						});
//						t2.start();
						 // DatagramSocket clientSocket = new DatagramSocket();
						  InetAddress IPAddress = InetAddress.getByName("172.97.103.33");
						 // InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
					      byte[] sendData =Arrays.copyOf(bts, cnt);
					      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					      clientSocket.send(sendPacket);
						//转换为输入流
//						 //定义字节数组输入输出流
//						ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOf(bts, cnt));
//						AudioFormat af2 = getAudioFormat();
//						// 定义音频输入流
//						AudioInputStream ais = new AudioInputStream(bais, af2, cnt / af2.getFrameSize());
//
//						try {
//							DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af2);
//							// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
//							SourceDataLine sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//							sd.open(af2);
//							sd.start();
//							// 创建播放进程
//							Thread t2 = new Thread(() -> {
//								byte btstmp[] = new byte[20000];
//								try {
//									int cntTmp;
//									// 读取数据到缓存数据
//									while ((cntTmp = ais.read(btstmp, 0, btstmp.length)) != -1) {
//										if (cntTmp > 0) {
//											// 写入缓存数据
//											// 将音频数据写入到混频器
//											sd.write(btstmp, 0, cntTmp);
//										}
//									}
//									System.out.println("play thread stop");
//
//								} catch (Exception e) {
//									e.printStackTrace();
//								} finally {
//									sd.drain();
//									sd.close();
//								}
//							});
//							t2.start();
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							try {
//								// 关闭流
//								if (ais != null) {
//									ais.close();
//								}
//								if (bais != null) {
//									bais.close();
//								}
//
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				td.drain();
				td.close();
			}
		});
		t1.start();
	}

	// 设置AudioFormat的参数
	public static AudioFormat getAudioFormat() {
		// 下面注释部分是另外一种音频格式，两者都可以
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 8000f;
		int sampleSize = 16;
		String signedString = "signed";
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
//			//采样率是每秒播放和录制的样本数
//			float sampleRate = 16000.0F;
//			// 采样率8000,11025,16000,22050,44100
//			//sampleSizeInBits表示每个具有此格式的声音样本中的位数
//			int sampleSizeInBits = 16;
//			// 8,16
//			int channels = 1;
//			// 单声道为1，立体声为2
//			boolean signed = true;
//			// true,false
//			boolean bigEndian = true;
//			// true,false
//			return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}

}
