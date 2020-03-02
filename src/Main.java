import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		/*JButton btnServidor = new JButton("Servidor");
		btnServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				MarcoServidor marcoS = new MarcoServidor();

				marcoS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		btnServidor.setBounds(118, 61, 175, 58);
		frame.getContentPane().add(btnServidor);*/
		
		MarcoServidor marcoS = new MarcoServidor();
		marcoS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton btnCliente = new JButton("Cliente");
		btnCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				MarcoCliente marcoC = new MarcoCliente();
				marcoC.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		btnCliente.setBounds(118, 142, 175, 58);
		frame.getContentPane().add(btnCliente);
	}
}