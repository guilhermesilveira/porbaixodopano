package antologia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

public class Servidor implements Runnable {

	private final Class<?>[] controllers;

	public Servidor(Class<?>... controllers) {
		this.controllers = controllers;
	}

	public static void main(String[] args) {
		new Thread(new Servidor(ProdutosController.class)).start();
	}

	@Override
	public void run() {
		System.out.println("Servidor rodando");
		try {
			ServerSocket socket = new ServerSocket(8080);
			while (true) {
				Socket client = socket.accept();
				trata(client);
				client.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void trata(Socket client) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		String verboPathEProtocolo = input.readLine();
		if(verboPathEProtocolo == null) return;
		System.out.println(verboPathEProtocolo);
		
		List<String> cabecalhos = new ArrayList<>(); 
		while(true) {
			String cabecalho = input.readLine();
			if(cabecalho == null || cabecalho.equals("")) break;
			cabecalhos.add(cabecalho);
		}
		String verbo = verboPathEProtocolo.split(" ")[0];
		String caminho = verboPathEProtocolo.split(" ")[1];
		new FrontController(controllers).processa(verbo, caminho, client.getOutputStream());
		
		input.close();
	}

}
