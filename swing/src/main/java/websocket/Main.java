package websocket;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;
import javax.swing.*;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class Main {

	// We used CountDownLatch to make sure that main thread does not exit after
	// executing the code. The main thread waits till the time latch decrements the
	// counter in onClose() method.
	private static CountDownLatch latch;

	private static final JFrame frame = new JFrame("Allegro");
	private static final JTextArea textArea = new JTextArea();
	private static final JTextField tf_name = new JTextField();
	private static final JTextField tf_first = new JTextField();
	private static final JTextField tf_dob = new JTextField();
	private static final JTextField tf_zip = new JTextField();
	private static final JTextField tf_ort = new JTextField();
	private static final JTextField tf_street = new JTextField();
	private static final JTextField tf_hausnr = new JTextField();
	private static final JTextField tf_ze_iban = new JTextField();
	private static final JTextField tf_ze_bic = new JTextField();
	private static final JTextField tf_ze_valid_from = new JTextField();

	private static final JRadioButton rb_female = new JRadioButton("Weiblich");
	private static final JRadioButton rb_male = new JRadioButton("Männlich");
	private static final JRadioButton rb_diverse = new JRadioButton("Divers");
	private static final ButtonGroup bg_gender = new ButtonGroup();

	private static final JsonParserFactory jsonParserFactory = Json.createParserFactory(null);

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
		panel.add(textArea, c);

		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		JButton button = new JButton("Anordnen");
		button.addActionListener(_ -> System.out.println("Button clicked!"));
		panel.add(button, c);

		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 650);
		frame.setVisible(true);
	}

	@javax.websocket.ClientEndpoint
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
		 * @param json The text message in JSON format
		 */
		@OnMessage
		public void onMessage(String json) {
			var message = extract(json);
			switch (message.target()) {
				case "textarea" -> textArea.setText(message.content());
				case "textfield" -> {
					var searchResult = toSearchResult(message.content());
					tf_name.setText(searchResult.name());
					tf_first.setText(searchResult.first());
					tf_dob.setText(searchResult.dob());
					tf_zip.setText(searchResult.zip());
					tf_ort.setText(searchResult.ort());
					tf_street.setText(searchResult.street());
					tf_hausnr.setText(searchResult.hausnr());
					tf_ze_iban.setText(searchResult.ze_iban());
					tf_ze_bic.setText(searchResult.ze_bic());
					tf_ze_valid_from.setText(searchResult.ze_valid_from());
				}
				default -> System.out.println("Unknown target: " + message.target());
			}
		}

		public void sendMessage(String message) {
			this.userSession.getAsyncRemote().sendText(message);
		}

		/**
		 * Extracts {@code target} and {@code content} fields from a JSON object.
		 * When the target is "textarea" the content field value is used directly;
		 * otherwise the full raw JSON string is returned as the content.
		 */
		public static Message extract(String json) {
			var values = parseJsonStringFields(json);
			var target  = values.getOrDefault("target", "");
			var rawContent = values.getOrDefault("content", "");
			var content = "textarea".equals(target) ? rawContent : json;
			return new Message(target, content);
		}
	}

	/** Parses a flat JSON object and returns a map of all string-valued fields. */
	private static Map<String, String> parseJsonStringFields(String json) {
		var values = new HashMap<String, String>();
		var jsonParser = jsonParserFactory.createParser(new StringReader(json));
		String currentKey = null;
		while (jsonParser.hasNext()) {
			var event = jsonParser.next();
			if (Event.KEY_NAME.equals(event)) {
				currentKey = jsonParser.getString();
			} else if (Event.VALUE_STRING.equals(event) && currentKey != null) {
				values.put(currentKey, jsonParser.getString());
				currentKey = null;
			}
		}
		return values;
	}

	/** Immutable data-transfer object representing a routed WebSocket message. */
	record Message(String target, String content) {}

	/** Immutable value object holding all search-result fields from the server. */
	record SearchResult(
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

	public static SearchResult toSearchResult(String json) {
		var v = parseJsonStringFields(json);
		return new SearchResult(
				v.getOrDefault("name",       ""),
				v.getOrDefault("first",      ""),
				v.getOrDefault("dob",        ""),
				v.getOrDefault("zip",        ""),
				v.getOrDefault("ort",        ""),
				v.getOrDefault("street",     ""),
				v.getOrDefault("hausnr",     ""),
				v.getOrDefault("iban",       ""),
				v.getOrDefault("bic",        ""),
				v.getOrDefault("valid_from", "")
		);
	}
}

