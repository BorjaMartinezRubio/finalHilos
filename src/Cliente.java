import javax.swing.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

public class Cliente {

}

class MarcoCliente extends JFrame {
	public MarcoCliente() {
		setBounds(600, 300, 280, 350);
		LaminaMarcoCliente lamina = new LaminaMarcoCliente();
		add(lamina);
		setVisible(true);
		addWindowListener(new EnvioOnline());// Nada mas abrir la lamina se activara envio online y se
												// creara el socket correspondiente para enviarle
												// el objeto y guardaremos la variable mensaje para saber que
												// el cliente esta online
	}
}

// Envia online
class EnvioOnline extends WindowAdapter { // Clase implement
	public void windowOpened(WindowEvent e) { // Se ejecuta al abrir la ventana
		try {
			Socket socket = new Socket("localhost", 5555);
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setMensaje(" conectado");

			ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
			paquete_datos.writeObject(datos);
			socket.close();
		} catch (Exception e2) {

		}
	}
}

class LaminaMarcoCliente extends JPanel implements Runnable { // Primera clase que se ejecuta al abrir el cliente donde
																// se
																// encuentra la lamina, el boton etc..
	private JTextField campoMen;
	private JTextField puertoDestinatario;
	private JLabel nombre;
	private JTextArea campochat;
	private JButton botonE;
	private ArrayList<String> ListaPuertos;
	private String puertoCliente;
	private String puertoServidor;
	private RegistrosC regc;
	private int contador = 1;
	public int contador2 = 0;


	public LaminaMarcoCliente() {
		String nombreU = JOptionPane.showInputDialog("Nombre: "); // Pido nick y guardo

		JLabel labelNick = new JLabel("Nombre: ");
		add(labelNick);

		nombre = new JLabel();
		add(nombre);
		nombre.setText(nombreU);

		JLabel labelTex = new JLabel("Destino: ");
		add(labelTex);

		//puertoCliente = JOptionPane.showInputDialog("Introduce tu puerto: "); // En esta variable guardo el
																				// puerto del cliente
		puertoCliente = String.valueOf(contador);
		contador++;
		
		/*puertoDestinatario = new JTextField(10);
		add(puertoDestinatario);*/

		//puertoServidor = JOptionPane.showInputDialog("Introduce puerto Servidor: "); // En esta variable guardo
																						// el puerto del cliemte
		puertoServidor = String.valueOf(9999);
		
		campochat = new JTextArea(12, 20);
		campochat.setText("Server: Bienvenido \n              Elija su menu: \n              Menu 1 \n              Menu 2 \n              Menu 3");
		add(campochat);

		campoMen = new JTextField(20);
		add(campoMen);

		botonE = new JButton("Enviar");

		EnviaTexto evento = new EnviaTexto();
		botonE.addActionListener(evento);
		add(botonE);

		Thread hC = new Thread(this);
		hC.start();
	}

	private class EnviaTexto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			campochat.append("\n" + campoMen.getText()); // Escribimos en el text area del cliente
			regc = new RegistrosC("ActividadCliente.txt");

			try {
				Socket socket = new Socket("localhost", Integer.parseInt(puertoServidor));
				PaqueteEnvio datos = new PaqueteEnvio();

				datos.setNombre(nombre.getText()); // Envio texto al objeto
				//datos.setPuertoDestinatario(puertoDestinatario.getText()); // Envio puerto del cliente destinatario al
																			// objeto
				datos.setPuertoC(puertoCliente); //// Envio puerto propio del cliente al objeto
				datos.setMensaje(campoMen.getText()); // cojo los datos del mensaje
				
				conseguirPaso(datos);
				
				datos.setPaso(contador2);
			
				contador2 = datos.getPaso();
				
				regc.write("\n" + datos.getNombre() + " dice " + campoMen.getText() + " al puerto "
						/*+ datos.getPuertoDestinatario()*/); // Escribimos en el fichero

				ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream()); // Creo flujo de
																										// datos de
																										// salida al
																										// objeto
				paquete_datos.writeObject(datos); // Enviamos el objeto al socket determinado
				socket.close();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		public void conseguirPaso (PaqueteEnvio datos) {
			if(datos.getMensaje().equals("1")||datos.getMensaje().equals("2")||datos.getMensaje().equals("3") && contador2 == 0) {
				contador2++;
				System.out.println("Ahora estoy en el paso "+ contador2);
					
			}
		}
	}

	@Override
	public void run() { // metodo run hilo
		

		try {
			ServerSocket servidor_cliente = new ServerSocket(Integer.parseInt(puertoCliente)); // Creo socket para
																								// cliente
																								// y paso el puerto
			Socket cliente;

			PaqueteEnvio paqueteRecibido; // objeto recibido con sus parametros correspondientes

			while (true) { // bucle infinito
				cliente = servidor_cliente.accept();
				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
				paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject(); // casting para que el objeto flujoentrada
																			// pueda usar los datos del tipo
																			// paqueteenvio de paqueterecibido
				if (!paqueteRecibido.getMensaje().equals(" online")) {
					campochat.append("\n" + paqueteRecibido.getNombre() + ": " + paqueteRecibido.getMensaje());
					if(paqueteRecibido.getMensaje().equalsIgnoreCase("Lo siento no le he entendido bien")) {
						contador2--;
					}
				} else {
					campochat.append("\n" + paqueteRecibido.getIps());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

class PaqueteEnvio implements Serializable {// transforma el objeto en binario para mandarlo por la red

	private String nombre, /*puertoDestinatario,*/ mensaje;
	private ArrayList<String> Ips;
	private String puertoC;
	private int paso;

	public String getPuertoC() {
		return puertoC;
	}

	public void setPuertoC(String puertoC) {
		this.puertoC = puertoC;
	}

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/*public String getPuertoDestinatario() {
		return puertoDestinatario;
	}*/

	/*public void setPuertoDestinatario(String puertoDestinatario) {
		this.puertoDestinatario = puertoDestinatario;
	}*/

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getPaso() {
		return paso;
	}

	public void setPaso(int paso) {
		this.paso = paso;
	}
}

class RegistrosC {
	String enlace = "ActividadCliente.txt"; // Enlace fichero
	private File registro = new File(enlace);

	public RegistrosC(String nombre_archivo) {
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