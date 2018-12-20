package cn.xvkang.udpaudio.stackoverflow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.LineUnavailableException;

public class UdpClient {
	public static class Client {
		InetAddress inetAddress;
		int port;
	}

	public static void main(String[] args) throws LineUnavailableException {
		ConcurrentHashMap<String, List<Client>> map = new ConcurrentHashMap<>();

//		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
//		TargetDataLine microphone;
//		SourceDataLine speakers;
//		microphone = AudioSystem.getTargetDataLine(format);
//
//		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//		microphone = (TargetDataLine) AudioSystem.getLine(info);
//		microphone.open(format);

		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numBytesRead;
		int CHUNK_SIZE = 1024;
//		byte[] data = new byte[microphone.getBufferSize() / 5];
//		microphone.start();
//
//		int bytesRead = 0;
//		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
//		speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//		speakers.open(format);
//		speakers.start();

		// String hostname = "localhost";
		// int port = 5555;

		try {
			// InetAddress address = InetAddress.getByName(hostname);
			DatagramSocket socket = new DatagramSocket();

			DatagramSocket serverSocket = new DatagramSocket(5555);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];

			while (true) {

				byte[] buffer = new byte[1024];
				DatagramPacket response = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(response);

				byte[] data2 = response.getData();
				String quote = new String(buffer, 0, response.getLength(), "UTF-8");
				if (quote != null && quote.equals("listener")) {
					map.remove("listener");
					List<Client> clients = new ArrayList<>();
					Client client = new Client();
					client.inetAddress = response.getAddress();
					client.port = response.getPort();
					clients.add(client);
					map.put("listener", clients);
					continue;
				} else if (quote != null && quote.equals("speaker")) {
					map.remove("speaker");
					List<Client> clients = new ArrayList<>();
					Client client = new Client();
					client.inetAddress = response.getAddress();
					client.port = response.getPort();
					clients.add(client);
					map.put("speaker", clients);
					continue;
				}

				// out.write(response.getData(), 0, response.getData().length);
				// 播放
				// speakers.write(data2, 0, data2.length);

				// 将数据发给所有的speaker
				// Thread sendThread = new Thread(() -> {
				List<Client> list = map.get("speaker");
				if (list != null) {
					for (Client client : list) {
						System.out.println(
								"send one package to" + client.inetAddress.getHostAddress() + ":" + client.port);
						DatagramPacket sendPacket = new DatagramPacket(data2, data2.length, client.inetAddress,
								client.port);
						serverSocket.send(sendPacket);
					}
				}
				// });
				// sendThread.start();

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