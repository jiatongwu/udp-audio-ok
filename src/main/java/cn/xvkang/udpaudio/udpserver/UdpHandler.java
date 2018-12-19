package cn.xvkang.udpaudio.udpserver;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class UdpHandler extends IoHandlerAdapter {

	private MinaUdpServer server;

	public UdpHandler(MinaUdpServer server) {
		this.server = server;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		session.closeNow();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("messageReceived" + session.toString());
		if (message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer) message;
			
			SocketAddress remoteAddress = session.getRemoteAddress();
			
			byte[] array = new byte[buffer.limit()];
			
			buffer.get(array);
			
			
			// 转换为输入流
			// 定义字节数组输入输出流
			ByteArrayInputStream bais = new ByteArrayInputStream(array);
			AudioFormat af2 = getAudioFormat();
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
					byte btstmp[] = new byte[35506];
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
						System.out.println("play thread stop");

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
			
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("Session closed..." + session.toString());
		SocketAddress remoteAddress = session.getRemoteAddress();
		server.getClients().remove(remoteAddress);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {

		System.out.println("Session created..." + session.toString());

		SocketAddress remoteAddress = session.getRemoteAddress();
		server.getClients().put(remoteAddress, session.toString());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Session idle...");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("Session Opened...");
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
//				//采样率是每秒播放和录制的样本数
//				float sampleRate = 16000.0F;
//				// 采样率8000,11025,16000,22050,44100
//				//sampleSizeInBits表示每个具有此格式的声音样本中的位数
//				int sampleSizeInBits = 16;
//				// 8,16
//				int channels = 1;
//				// 单声道为1，立体声为2
//				boolean signed = true;
//				// true,false
//				boolean bigEndian = true;
//				// true,false
//				return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
		}

}
