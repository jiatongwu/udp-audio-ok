package cn.xvkang.udpaudio.stackoverflow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class UdpClientSpeaker {

	public static void main(String[] args) throws LineUnavailableException {

		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
		TargetDataLine microphone;
		SourceDataLine speakers;
		microphone = AudioSystem.getTargetDataLine(format);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		microphone = (TargetDataLine) AudioSystem.getLine(info);
		microphone.open(format);

		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numBytesRead;
		int CHUNK_SIZE = 1024;
		byte[] data = new byte[microphone.getBufferSize() / 5];
		microphone.start();

		int bytesRead = 0;
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		speakers.open(format);
		speakers.start();

		// String hostname = "localhost";
		// int port = 5555;

		try {
			// InetAddress address = InetAddress.getByName(hostname);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			// Configure the ip and port
			String hostname = "wu2.host.funtoo.org";
			int port = 5555;
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(hostname);
			String message = "speaker";

			DatagramPacket sendMessage = new DatagramPacket(message.getBytes("UTF-8"), message.getBytes("UTF-8").length,
					address, port);
			socket.send(sendMessage);
			socket.send(sendMessage);
			
			while (true) {

				byte[] buffer = new byte[1024];
				DatagramPacket response = new DatagramPacket(buffer, buffer.length);
				socket.receive(response);
				byte[] data2 = response.getData();
				// out.write(response.getData(), 0, response.getData().length);
				// 播放
				 speakers.write(data2, 0, data2.length);
				// String quote = new String(buffer, 0, response.getLength());
				// System.out.println(quote);
				// System.out.println();
				// Thread.sleep(10000);
			}

		} catch (SocketTimeoutException ex) {
			System.out.println("Timeout error: " + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Client error: " + ex.getMessage());
			ex.printStackTrace();
		} /*
			 * catch (InterruptedException ex) { ex.printStackTrace(); }
			 */
	}
}