package antologia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.Channels;

public class FrontController {

	private final Class<?>[] controllers;

	FrontController(Class<?>... controllers) {
		this.controllers = controllers;
	}

	public void processa(String verbo, String caminho, OutputStream cliente)
			throws IOException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException {

		try (PrintStream output = new PrintStream(cliente)) {

			if (caminho.charAt(caminho.length() - 1) == '/') {
				caminho = caminho + "index.html";
			}
			String caminhoSemBarra = caminho.substring(1);
			File arquivo = new File("src/main/webapp/" + caminhoSemBarra);
			if (arquivo.exists()) {
				output.println("HTTP/1.1 200 OK");
				output.println("Content-Type: text/html; charset=utf-8");
				output.println("");
				output.flush();

				new FileInputStream(arquivo).getChannel().transferTo(0l,
						arquivo.length(), Channels.newChannel(cliente));
				return;
			}

			for (Class<?> controller : controllers) {
				if (caminhoSemBarra.equals(controller.getSimpleName().replace("Controller", "").toLowerCase())) {
					output.println("HTTP/1.1 200 OK");
					output.println("Content-Type: text/html; charset=utf-8");
					output.println("");
					output.flush();

					Object instancia = controller.newInstance();
					Method metodo = controller.getDeclaredMethod("index");
					String resultado = (String) metodo.invoke(instancia);
					output.println(resultado);
					return;
				}
			}

			output.println("HTTP/1.1 404 Not Found");
			output.println("Content-Type: text/html; charset=utf-8");
			output.println("");
			output.println("<html>");
			output.println("<h1>" + caminhoSemBarra + " não encontrado</h1>");
			output.println("</html>");
		}
	}

}
