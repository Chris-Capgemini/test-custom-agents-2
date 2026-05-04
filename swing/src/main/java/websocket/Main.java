package websocket;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import jakarta.json.Json;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import javax.swing.*;

public class Main {

	// We used CountDownLatch to make sure that main thread does not exit after
	// executing the code. The main thread waits till the time latch decrements the
	// counter in onClose() method.
	private static CountDownLatch latch;

	private static JFrame frame = new JFrame("Allegro");
	private static JTextArea textArea = new JTextArea();
	private static JTextField tf_name = new JTextField();
	private static JTextField tf_first = new JTextField();
	private static JTextField tf_dob = new JTextField();
	private static JTextField tf_zip = new JTextField();
	private static JTextField tf_ort = new JTextField();
	private static JTextField tf_street = new JTextField();
	private static JTextField tf_hausnr = new JTextField();
	private static JTextField tf_ze_iban = new JTextField();
	private static JTextField tf_ze_bic = new JTextField();
	private static JTextField tf_ze_valid_from = new JTextField();

	private static JRadioButton rb_female = new JRadioButton("Weiblich");
	private static JRadioButton rb_male = new JRadioButton("Männlich");
	private static JRadioButton rb_diverse = new JRadioButton("Divers");
	private static ButtonGroup bg_gender = new ButtonGroup();

	public static void main(String[] args) throws IOException, DeploymentException {
		initUI();

		latch = new CountDownLatch(1);

		String uri = "ws://localhost:1337/";
		System.out.println("Connecting to " + uri);

		// open websocket
		final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(URI.create(uri));
		// clientEndPoint.sendMessage("{''}");
	}

	private static void initUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 4;
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Vorname"), c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_first, c);
		
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Name"), c);

		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_name, c);

		c.gridx = 4;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Geburtsdatum"), c);

		c.gridx = 5;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_dob, c);

		bg_gender.add(rb_female);
		bg_gender.add(rb_male);
		bg_gender.add(rb_diverse);
		rb_female.setSelected(true);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.CENTER;
		panel.add(new JLabel("Geschlecht"), c);

		JPanel genderPanel = new JPanel();
		genderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		genderPanel.add(rb_female);
		genderPanel.add(rb_male);
		genderPanel.add(rb_diverse);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.WEST;
		panel.add(genderPanel, c);

		// Reset grid layout
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_END;

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Strasse"), c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_street, c);
		
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("PLZ"), c);

		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_zip, c);
		
		c.gridx = 4;
		c.gridy = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Ort"), c);

		c.gridx = 5;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_ort, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("IBAN"), c);

		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_ze_iban, c);
		
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("BIC"), c);

		c.gridx = 3;
		c.gridy = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_ze_bic, c);
		
		c.gridx = 4;
		c.gridy = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Gültig ab"), c);

		c.gridx = 5;
		c.gridy = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tf_ze_valid_from, c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("RT"), c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 6;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		textArea.setPreferredSize(new Dimension(200, 400));
		textArea.setBorder(BorderFactory.createEtchedBorder());
		panel.add(textArea);
		panel.add(textArea, c);

		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		JButton button = new JButton("Anordnen");
		button.addActionListener(e -> {
			System.out.println("Button clicked!");
		});
		panel.add(button, c);

		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 650);
		frame.setVisible(true);
	}

	@ClientEndpoint
	public static class WebsocketClientEndpoint {

		Session userSession = null;

		public WebsocketClientEndpoint(URI endpointURI) {
			try {
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				container.connectToServer(this, endpointURI);
				latch.await();
			} catch (DeploymentException | IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Callback hook for Connection open events.
		 *
		 * @param userSession the userSession which is opened.
		 */
		@OnOpen
		public void onOpen(Session userSession) {
			System.out.println("opening websocket");
			this.userSession = userSession;
		}

		/**
		 * Callback hook for Connection close events.
		 *
		 * @param userSession the userSession which is getting closed.
		 * @param reason      the reason for connection close
		 */
		@OnClose
		public void onClose(Session userSession, CloseReason reason) {
			System.out.println("closing websocket");
			this.userSession = null;
			latch.countDown();
		}

		/**
		 * Callback hook for Message Events. This method will be invoked when a client
		 * send a message.
		 *
		 * @param json The text message (JSON payload)
		 */
		@OnMessage
		public void onMessage(String json) {
			Message message = extract(json);
			switch (message.target()) {
				case "textarea" -> textArea.setText(message.content());
				case "textfield" -> {
					SearchResult sr = toSearchResult(message.content());
					tf_name.setText(sr.name());
					tf_first.setText(sr.first());
					tf_dob.setText(sr.dob());
					tf_zip.setText(sr.zip());
					tf_ort.setText(sr.ort());
					tf_street.setText(sr.street());
					tf_hausnr.setText(sr.hausnr());
					tf_ze_iban.setText(sr.ze_iban());
					tf_ze_bic.setText(sr.ze_bic());
					tf_ze_valid_from.setText(sr.ze_valid_from());
				}
			}
		}

		public void sendMessage(String message) {
			this.userSession.getAsyncRemote().sendText(message);
		}

		/**
		 * Extracts a {@link Message} from a JSON string containing {@code target} and
		 * {@code content} fields. When the target is {@code "textarea"} the
		 * {@code content} field value is used; for all other targets the raw JSON
		 * string is forwarded as-is (it will be re-parsed by {@link #toSearchResult}).
		 */
		public static Message extract(String json) {
			try (var reader = Json.createReader(new StringReader(json))) {
				var obj = reader.readObject();
				var target = obj.getString("target", "");
				var content = "textarea".equals(target) ? obj.getString("content", "") : json;
				return new Message(target, content);
			}
		}
	}

	/** Immutable value object representing a WebSocket message. */
	private record Message(String target, String content) {}

	/** Immutable value object representing a person-search result payload. */
	private record SearchResult(
			String name,
			String first,
			String dob,
			String zip,
			String ort,
			String street,
			String hausnr,
			String ze_iban,
			String ze_bic,
			String ze_valid_from) {}

	/**
	 * Deserialises a JSON object string into a {@link SearchResult}.
	 * Uses Jakarta JSON-P {@code JsonReader} for clean, concise parsing.
	 */
	public static SearchResult toSearchResult(String json) {
		try (var reader = Json.createReader(new StringReader(json))) {
			var obj = reader.readObject();
			return new SearchResult(
					obj.getString("name", ""),
					obj.getString("first", ""),
					obj.getString("dob", ""),
					obj.getString("zip", ""),
					obj.getString("ort", ""),
					obj.getString("street", ""),
					obj.getString("hausnr", ""),
					obj.getString("iban", ""),
					obj.getString("bic", ""),
					obj.getString("valid_from", ""));
		}
	}
}
