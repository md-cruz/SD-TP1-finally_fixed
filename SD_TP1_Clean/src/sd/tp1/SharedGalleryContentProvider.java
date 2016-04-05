package sd.tp1;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sd.clt.ws.FileServerImplWSService;
import sd.clt.ws.FileServerImplWS;
import sd.tp1.gui.GalleryContentProvider;
import sd.tp1.gui.Gui;
import sd.tp1.gui.impl.GalleryWindow;

/*
 * This class provides the album/picture content to the gui/main application.
 * 
 * Project 1 implementation should complete this class. 
 */
public class SharedGalleryContentProvider implements GalleryContentProvider {

	Gui gui;
	List<String> servers;

	SharedGalleryContentProvider() {
		servers = new ArrayList<String>();
		getServers(servers);
		// gui = new GalleryWindow(this);
	}

	// to finish - catch interrupted exception
	private boolean youAlive(FileServerImplWS server) {
		boolean executed = false;
		boolean result = false;
		for (int i = 0; !executed && i < 3; i++) { // number of tries
			try {
				server.alive(result);
				executed = true;
			} catch (RuntimeException e) {
				if (i < 2) {
					try { // wait some time
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO: catch exception
					}
				}
			}
		}
		return result;
	}

	private void getServers(List<String> servers) {
		new Thread(() -> {
			try {

				final int port = 9000;
				final String addr = "228.0.0.1";
				final InetAddress address = InetAddress.getByName(addr);

				MulticastSocket socket = new MulticastSocket();

				byte[] input = ("GiveMeYourIps").getBytes();
				DatagramPacket packet = new DatagramPacket(input, input.length);
				packet.setAddress(address);
				packet.setPort(port);

				while (true) {
					System.out.println("Sent packet");
					socket.send(packet);
					// System.out.println(new String(packet.getData()));

					byte[] received = new byte[65536];
					DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
					boolean foundAllServers = false;
					try {
						while (!foundAllServers) {
							socket.setSoTimeout(60000);

							socket.receive(receivedPacket);

							String serverHost = new String(receivedPacket.getData()).trim();
							System.out.println(serverHost);
							if (!servers.contains(serverHost))
								servers.add(serverHost);
						}
					} catch (SocketTimeoutException e) {
						foundAllServers = true;
					}

					Thread.sleep(60000); // esperar um minuto e executar novo
											// multicast
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
		}).start();
	}

	/**
	 * Downcall from the GUI to register itself, so that it can be updated via
	 * upcalls.
	 */
	@Override
	public void register(Gui gui) {
		if (this.gui == null) {
			this.gui = gui;
		}
	}

	/**
	 * Returns the list of albums in the system. On error this method should
	 * return null.
	 */
	@Override
	public List<Album> getListOfAlbums() {
		List<Album> lst = new ArrayList<Album>();

		for (String serverUrl : servers) {
			try {
				System.out.println(serverUrl + " listAlbum\n");
				URL wsURL = new URL(String.format("%s", serverUrl));
				FileServerImplWSService service = new FileServerImplWSService(wsURL); // wsimport
				FileServerImplWS server = service.getFileServerImplWSPort();
				System.out.println("Encontrou server");
				try {
					List<String> aList = server.getAlbumList();
					for (String album : aList) {
						SharedAlbum alb = new SharedAlbum(album);
						if (!lst.contains(alb)&& !album.endsWith(".deleted"))
							lst.add(alb);
					}
					System.out.println("fez fixe");
					
				} catch (Exception e) {
					// call method again, max 3 times
					boolean executed = false;
					System.out.println("fez merda");
					for (int i = 0; !executed && i < 3; i++) { // number of
																// tries
						try {
							List<String> aList = server.getAlbumList();
							for (String album : aList) {
								SharedAlbum alb = new SharedAlbum(album);
								if (!lst.contains(alb))
									lst.add(alb);
							}
							executed = true;
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Failed to connect to server " + serverUrl);
				return null;
			}
		}
		return lst;
	}

	/**
	 * Returns the list of pictures for the given album. On error this method
	 * should return null.
	 */
	@Override
	public List<Picture> getListOfPictures(Album album) {

		List<Picture> lst = new ArrayList<Picture>();
		
			for (String serverUrl : servers) {
				try {
				System.out.println(serverUrl + " listPicture\n");
				URL wsURL = new URL(String.format("%s", serverUrl));
				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					List<String> picList = server.getPictureList(album.getName());
					for (String pic : picList) {
						SharedPicture picture = new SharedPicture(pic);
						if (!lst.contains(picture)&& !pic.endsWith(".deleted"))
							lst.add(picture);
					}
				} catch (Exception e) {
					// call method again, max 3 times
					boolean executed = false;
					for (int i = 0; !executed && i < 3; i++) { // number of
																// tries
						try {
							List<String> picList = server.getPictureList(album.getName());
							for (String pic : picList) {
								SharedPicture picture = new SharedPicture(pic);
								if (!lst.contains(picture))
									lst.add(picture);
							}
							executed = true;
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}
			}
		 catch (Exception e) {
			 e.printStackTrace();
			System.out.println("Failed to connect to server " + serverUrl);
			return null;
		}
			}
		return lst;
	}

	/**
	 * Returns the contents of picture in album. On error this method should
	 * return null.
	 */
	@Override
	public byte[] getPictureData(Album album, Picture picture) {
		byte[] pictureData = null;
		

			for (String serverUrl : servers) {
				try {
				System.out.println(serverUrl + " downloadPicture\n");
				URL wsURL = new URL(String.format("%s", serverUrl));
				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					pictureData = server.downloadPicture(album.getName(), picture.getName());
					return pictureData;
				} catch (Exception e) {
					// call method again, max 3 times

					for (int i = 0; i < 3; i++) { // number of tries
						try {
							pictureData = server.downloadPicture(album.getName(), picture.getName());
							return pictureData;
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}
			}

		 catch (Exception e) {
			System.out.println("Failed to connect to server " + serverUrl);
			return null;
		}
			}
		return pictureData;
	}

	/**
	 * Create a new album. On error this method should return null.
	 */
	@Override
	public Album createAlbum(String name) {
		int i = 0;
		int times = 0;
		final int[] serverIndexes = new Random().ints(0, servers.size()).distinct().limit(servers.size()).toArray();
		boolean finished = false;
		
		while (!finished && i < servers.size()) {
			try {

				System.out.println(servers.get(i) + " createAlbum\n");
				URL wsURL = new URL(String.format("%s", servers.get(serverIndexes[i])));
				i++;
				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					finished = server.createNewAlbum(name);
					
				} catch (Exception e) {
					// call method again, max 3 times
					boolean executed = false;
					for (int j = 0; !executed && j < 3; j++) { // number of
																// tries
						try {
							finished = server.createNewAlbum(name);
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}
			} catch (Exception e) {
				if (i >= servers.size()) {
					if (times < 2) {
						i = 0;
						times++;
					} else
						break;
				}
			}
		}
		System.out.println(finished);
		if (!finished)
			return null;
		return new SharedAlbum(name);
	}

	/**
	 * Delete an existing album.
	 */
	@Override
	public void deleteAlbum(Album album) {

		for (String serverUrl : servers) {
			try {
				System.out.println(serverUrl + " deleteAlbum\n");
				URL wsURL = new URL(String.format("%s", serverUrl));
				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					server.deleteAlbum(album.getName());
				} catch (Exception e) {
					// call method again, max 3 times
					boolean executed = false;
					for (int i = 0; !executed && i < 3; i++) { // number of
																// tries
						try {
							server.deleteAlbum(album.getName());
							System.out.println("Deleted");
							executed = true;
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}
			}

			catch (Exception e) {
				System.out.println("Failed to delete album " + album.getName());
			}
		}
	}

	/**
	 * Add a new picture to an album. On error this method should return null.
	 */
	@Override
	public Picture uploadPicture(Album album, String name, byte[] data) {

		int i = 0;
		int times = 0;
		final int[] serverIndexes = new Random().ints(0, servers.size()).distinct().limit(servers.size()).toArray();

		boolean finished = false;
		while (!finished && i < servers.size()) {

			try {
				System.out.println(servers.get(i) + " uploadPicture\n");
				URL wsURL = new URL(String.format("%s", servers.get(serverIndexes[i])));
				i++;

				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					server.uploadPicture(album.getName() + "/" + name, data);
					finished = true;
				} catch (Exception e) {
					// call method again, max 3 times
					for (int j = 0; !finished && j < 3; j++) { // number of
																// tries
						try {
							server.uploadPicture(album.getName() + "/" + name, data);
							finished = true;

						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}

			} catch (Exception e) {
				if (i >= servers.size()) {
					if (times < 2) {
						i = 0;
						times++;
					} else
						break;
				}

			}
		}
		if (!finished)
			return null;
		return new SharedPicture(name);
	}

	/**
	 * Delete a picture from an album. On error this method should return false.
	 */
	@Override
	public boolean deletePicture(Album album, Picture picture) {
		boolean finished = false;
		for (String serverUrl : servers) {
			System.out.println(serverUrl + " deletePicture\n");
			try {
				URL wsURL = new URL(String.format("%s", serverUrl));
				FileServerImplWSService service = new FileServerImplWSService(wsURL);
				FileServerImplWS server = service.getFileServerImplWSPort();
				try {
					server.deletePicture(album.getName(), picture.getName());
					finished = true;
					break;
				} catch (Exception e) {
					// call method again, max 3 times
					for (int i = 0; !finished && i < 3; i++) {
						try {
							server.deletePicture(album.getName(), picture.getName());
							finished = true;
						} catch (RuntimeException e1) {
							if (i < 2) {
								try { // wait some time
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									// do nothing
								}
							}
						}
					}
				}
			}

			catch (Exception e) {
				// exception handled?
				System.out.println("Failed to connect to server " + serverUrl);

			}
		}
		return finished;
	}

	/**
	 * Represents a shared album.
	 */
	static class SharedAlbum implements GalleryContentProvider.Album {
		final String name;

		SharedAlbum(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	/**
	 * Represents a shared picture.
	 */
	static class SharedPicture implements GalleryContentProvider.Picture {
		final String name;

		SharedPicture(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
