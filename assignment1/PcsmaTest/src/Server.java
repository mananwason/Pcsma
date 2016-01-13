
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private static int PORT = 6300;
	private String destinationPath = "/Users/pradeepwason/Desktop/accelerometer2.csv";

	public Server() {

	}

	public void connect() {
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			serverSocket = new ServerSocket(PORT);
			InetAddress IP = InetAddress.getLocalHost();
			System.out.println("IP of my system is := " + IP.getHostAddress() + ":" + PORT);
			while (true) {
				socket = serverSocket.accept();
				byte[] mybytearray = new byte[6022386];
				InputStream is = socket.getInputStream();
				fos = new FileOutputStream(destinationPath);
				bos = new BufferedOutputStream(fos);
				bytesRead = is.read(mybytearray, 0, mybytearray.length);
				current = bytesRead;

				do {
					bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
					if (bytesRead >= 0)
						current += bytesRead;
				} while (bytesRead > -1);

				bos.write(mybytearray, 0, current);
				bos.flush();
				System.out.println("File " + destinationPath + " downloaded (" + current + " bytes "
						+ "read) at epochtime :" + System.currentTimeMillis());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {

				if (fos != null)
					fos.close();
				if (bos != null)
					bos.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
