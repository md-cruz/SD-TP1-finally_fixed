package sd.srv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class FileServerImplWS {
	private static final String WSERVICE = "FileServerWebService";
	private static final String LOCALHOST = "http://localhost:8080/FileServer";
	private File basePath;

	public FileServerImplWS() {
		this(".");
	}

	protected FileServerImplWS(String pathname) {
		super();
		basePath = new File(pathname);
	}

	@WebMethod
	public FileInfo getFileInfo(String path) throws InfoNotFoundException {
		File f = new File(basePath, path);
		if (f.exists())
			return new FileInfo(f.getName(), f.length(), new Date(f.lastModified()), f.isFile());
		else
			throw new InfoNotFoundException("File not found :" + path);
	}
	
	@WebMethod
	public String[] getFileList (String path) throws InfoNotFoundException {
		File f = new File(basePath, path);
		if(f.exists() && f.isDirectory())
			return f.list();
		else
			throw new InfoNotFoundException("File not found :" + path);
	
	}

	@WebMethod
	public byte[] getFile (String path) throws InfoNotFoundException, IOException {
		File f = new File(basePath,path);
		if(f.exists() && f.isFile()){
			FileInputStream sIn = new FileInputStream(f);
			byte[] info = new byte[(int) f.length()];
			sIn.read(info);
			sIn.close();
			return info;
		}
		else
			throw new InfoNotFoundException("File not found :" + path);
	}
	
	
	@WebMethod
	public void alive (boolean status){
		status = true;
	}
	
	@WebMethod
	public void uploadFile(String path, byte[] data) throws InfoNotFoundException,IOException {
		// TODO: implement method
	}
	
	public static void main(String args[]) throws Exception {
		String path = args.length > 0 ? args[0] : ".";
		Endpoint.publish("http://0.0.0.0:8080/FileServer", new FileServerImplWS(path));
		System.err.println("FileServer started");

		final String addr = "228.0.0.1";

		final InetAddress address = InetAddress.getByName(addr);
		MulticastSocket socket = new MulticastSocket(9000);
		socket.joinGroup(address);
		while (true) {
			byte[] buffer = new byte[65536];
			
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String s = new String(packet.getData()).trim();
			System.out.println(s);
			if (s.equalsIgnoreCase(WSERVICE)){
				
				byte[] data = LOCALHOST.getBytes();
				DatagramPacket sendingPacket = new DatagramPacket(data,data.length);
				sendingPacket.setAddress(packet.getAddress());
				sendingPacket.setPort(packet.getPort());
				socket.send(sendingPacket);
			}
		}
	}
}
