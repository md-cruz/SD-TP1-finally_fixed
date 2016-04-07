package sd.rest.srv;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;


public class RestServer {
	private static final String WSERVICE = "GiveMeYourIps";
	private static final String LOCALHOST = "http://localhost:8080/FileServer";
	
	public static void main(String[] args) throws Exception {

		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(9090).build();

		ResourceConfig config = new ResourceConfig();
		try{
		ServerResource.basePath = new File(args[0]);
		}catch(Exception e){
			System.err.println("Please specify the server folder.\nClosing application...");
			return;
		}
	
		config.register(ServerResource.class);
		
		HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

		System.err.println("REST Server ready... ");
		
		final String addr = "228.0.0.1";

		final InetAddress address = InetAddress.getByName(addr);
		MulticastSocket socket = new MulticastSocket(9000);
		socket.joinGroup(address);
		while (true) {
			byte[] buffer = new byte[65536];
			
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String s = new String(packet.getData()).trim();
			
			if (s.equalsIgnoreCase(WSERVICE)){
				
				byte[] data = ("R"+LOCALHOST).getBytes();
				DatagramPacket sendingPacket = new DatagramPacket(data,data.length);
				sendingPacket.setAddress(packet.getAddress());
				sendingPacket.setPort(packet.getPort());
				socket.send(sendingPacket);
			}
		}
	}
}
