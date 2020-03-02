import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Servidor {

}

class MarcoServidor extends JFrame implements Runnable {

	public MarcoServidor() {
		// puertoServer = JOptionPane.showInputDialog("Abre Puerto Servidor: "); // Pido
		// puerto servidor
		puertoServer = String.valueOf(9999);
		setBounds(1200, 300, 280, 350);

		JPanel lamina = new JPanel();
		lamina.setLayout(new BorderLayout());

		areatexto = new JTextArea();
		lamina.add(areatexto, BorderLayout.CENTER);
		add(lamina);
		setVisible(true);

		Thread hS = new Thread(this);
		hS.start();
	}

	private RegistrosS regs;
	private JTextArea areatexto;
	private String puertoServer;

	@Override
	public void run() {

		try {
			ServerSocket servidor = new ServerSocket(Integer.parseInt(puertoServer)); // Creamos servidor
			String nombre, puertoDestinatario, mensaje, puertoC;
			int paso;
			String mensajeRes;

			PaqueteEnvio paquete_recibido; // Creamos objeto de la clase CLiente donde esta ubicada tambien la clase
											// PaqueteEnvio

			regs = new RegistrosS("ActividadServidor.txt");

			while (true) { // bucle infinito
				System.out.println("Escuchando...");
				Socket socket = servidor.accept();

				ObjectInputStream paquete_datos = new ObjectInputStream(socket.getInputStream());// flujo de dato de
																									// entrada
				paquete_recibido = (PaqueteEnvio) paquete_datos.readObject(); // Objeto recibido de cliente
				nombre = paquete_recibido.getNombre();
				// puertoDestinatario = paquete_recibido.getPuertoDestinatario();
				mensaje = paquete_recibido.getMensaje();
				puertoC = paquete_recibido.getPuertoC();
				paso = paquete_recibido.getPaso();

				areatexto.append("\n" + nombre + ": " + mensaje);
				regs.write("\n " + nombre + ": " + mensaje);

				if ((mensaje.equals("1") || mensaje.equals("2") || mensaje.equals("3")) && (paso == 1)) {
					mensajeRes = ("Gracias por elegir el " + mensaje);
					Socket enviaDestinatario = new Socket("localhost", Integer.parseInt(puertoC));// este
																									// socket
																									// envia
																									// la
																									// informacion
																									// al
																									// destinatario
																									// indicamos
																									// el
																									// puerto
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); // Aqui
																														// meto
																														// el
																														// paquete
																														// recibido
																														// anteriormente
																														// y
																														// enviarlo
																														// a
																														// los
																														// destinatarios
					paquete_recibido.setMensaje(mensajeRes);
					paquete_recibido.setNombre("Server");
					paqueteReenvio.writeObject(paquete_recibido);
					areatexto.append("\n Server: " + mensajeRes);
					regs.write("\n Server: " + mensajeRes);
					paqueteReenvio.close();
					enviaDestinatario.close();
					socket.close();
				} else {
					mensajeRes = ("Lo siento no le he entendido bien");
					Socket enviaDestinatario = new Socket("localhost", Integer.parseInt(puertoC));
					
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
					
					paquete_recibido.setMensaje(mensajeRes);
					paquete_recibido.setNombre("Server");
					paqueteReenvio.writeObject(paquete_recibido);
					areatexto.append("\n Server: " + mensajeRes);
					regs.write("\n Server: " + mensajeRes);
					paqueteReenvio.close();
					enviaDestinatario.close();
					socket.close();

				}
				System.out.println("Aceptado paquete de puerto " + paquete_recibido.getPuertoC());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

class RegistrosS {
	String enlace = "ActividadServidor.txt"; // Enlace fichero
	private File registro = new File(enlace);

	public RegistrosS(String nombre_archivo) {
		registro = new File(nombre_archivo);
	}

	public synchronized void write(String texto) {
		try {
			FileWriter fWriter = new FileWriter(registro, true);
			fWriter.write("[" + new Date() + "]" + " " + texto);
			fWriter.write("\r\n");
			fWriter.flush();
			fWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}