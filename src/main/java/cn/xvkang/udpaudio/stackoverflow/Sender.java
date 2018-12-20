package cn.xvkang.udpaudio.stackoverflow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Sender {

	public static void main(String[] args) throws IOException {

		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
		TargetDataLine microphone;
		SourceDataLine speakers;
		try {
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			//ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[microphone.getBufferSize() / 5];
			microphone.start();

			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
			speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			speakers.open(format);
			speakers.start();

			// Configure the ip and port
			String hostname = "wu2.host.funtoo.org";
			int port = 5555;
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(hostname);
			String message="listener";

			DatagramPacket sendMessage = new DatagramPacket(message.getBytes("UTF-8"), message.getBytes("UTF-8").length, address, port);
			socket.send(sendMessage);
			socket.send(sendMessage);
			
			byte[] buffer = new byte[1024];
			for (;;) {
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
				// bytesRead += numBytesRead;
				// write the mic data to a stream for use later
				//out.write(data, 0, numBytesRead);
				// write mic data to stream for immediate playback
				//speakers.write(data, 0, numBytesRead);
				DatagramPacket request = new DatagramPacket(data, numBytesRead, address, port);
				socket.send(request);

			}

		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}