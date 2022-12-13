package somali;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MyFrame implements ActionListener {
	private Boolean init = false;
	// Frame widgets instance variables
	private JProgressBar bar = null;
	private JButton start_button = null;
	private Thread progressThread = null;
	private List<JLabel> hearts = new ArrayList<>(3);
	private List<JButton> answer_buttons = new ArrayList<>(4);
	private List<Word> answer_options = new ArrayList<>(4);
	// Instance variables that hold key information
	private Word key_word = null;
	private JLabel current_word = null;
	private Boolean button_clicked = false;
	Boolean is_correct = false;
	Integer correct_index = -1;
	private boolean wait = false;
	
	public MyFrame() {
		// Initializing frame and setting default behavior
		UIManager.put("ProgressBar.foreground", new Color(255, 178, 102));
		init();
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);

		// JLabel for current word to translate
		JPanel word_panel = new JPanel(new GridBagLayout());
		word_panel.setBounds(200, 170, 200, 50);
		word_panel.setBackground(new Color(246, 246, 246));
		current_word = new JLabel("Press Start");
		current_word.setBounds(200, 70, 250, 250);
		current_word.setHorizontalAlignment(JLabel.CENTER);
		current_word.setFont(new Font("Arial", Font.BOLD, 25));
		word_panel.add(current_word);
		frame.add(word_panel);

		// Heart icons for number of chances remaining
		ImageIcon imageIcon = new ImageIcon("C:\\Users\\Zubeyr Farah\\Desktop\\hearts.png"); 
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(80, 70, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);
		JLabel heart = new JLabel(imageIcon);
		hearts.add(heart);
		heart.setBounds(1, 1, 50, 50);
		JLabel heart2 = new JLabel(imageIcon);
		hearts.add(heart2);
		heart2.setBounds(35, 1, 50, 50);
		JLabel heart3 = new JLabel(imageIcon);
		hearts.add(heart3);
		heart3.setBounds(70, 1, 50, 50);
		frame.add(heart);
		frame.add(heart2);
		frame.add(heart3);

		// Start and Stop button
		start_button = new JButton("Start");
		start_button.addActionListener(this);
		start_button.setFocusable(false);
		start_button.setFont(new Font("Arial", Font.BOLD, 15));
		start_button.setBounds(470, 15, 90, 40);
		start_button.setBackground(new Color(0, 255, 128));
		frame.add(start_button);

		// Progress bar to show the time elapsed
		bar = new JProgressBar();
		bar.setBorder(BorderFactory.createLineBorder(new Color(255, 178, 102)));
		bar.setMaximum(5000);
		bar.setBounds(50, 90, 500, 50);
		frame.add(bar);
		
		// Initializing answer choice buttons
		JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
		panel.setBounds(50, 260, 500, 200);
		for (int i = 0; i < 4; i++) {
			JButton button = new JButton("- - -");
			button.addActionListener(this);
			button.setBorder(BorderFactory.createLineBorder(Color.black));
			button.setFocusable(false);
			button.setFont(new Font("Arial", Font.PLAIN, 20));
			button.setBackground(new Color(246, 246, 246));
			button.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					if (button.getBackground().equals(new Color(246, 246, 246)))
						button.setBackground(new Color(240, 240, 240));
				}

				public void mouseExited(java.awt.event.MouseEvent evt) {
					if (button.getBackground().equals(new Color(240, 240, 240)))
						button.setBackground(new Color(246, 246, 246));
				}
			});
			answer_buttons.add(button);
			panel.add(button);
		}
		frame.getContentPane().setBackground(new Color(246, 246, 246));
		frame.add(panel);
		frame.setVisible(true);
	}
	
	/* Initializer function that starts the manager class and sets the languages
	 * Future iterations will allow language selection
	 */
	private void init() {
		boolean start_manager = Manager.setLanguage("Somali", "English");
		if (start_manager == false) {
			System.out.println("Failed to initialize languages");
			return;
		}
	}
	
	/* ClickEvent function for start/stop and answer buttons */
	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		// If button clicked is start_button then start if text is "start" else reset
		if (e.getSource() == start_button) {
			String text = start_button.getText();
			if (text.equals("Start")) {
				start_button.setText("Stop");
				start();
			} else {
				start_button.setText("Start");
				bar.setValue(0);
				progressThread.interrupt();
				stop(0);
			}
		} else {  // Handle answer button click by checking if correct and setting button_clicked to true to alert progress thread		
			JButton button = (JButton) e.getSource();
			String text = button.getText();
			is_correct = false;
			correct_index = -1;
			if (text.equals("- - -")) {
				return;
			}
			if (wait) {
				return;
			}
			wait = true;
			for (Word word : answer_options) {
				if (word.isEquivalent(key_word) && word.toString().equals(text)) {
					is_correct = true;
					correct_index = answer_options.indexOf(word);
				}
			}
			button_clicked = true;
		}
	}

	/* Function changes the style of the buttons when an answer is selected */
	public void evaluateSelection(Boolean bool, Integer correct_index) {
		Color green = new Color(144,238,144);
		Color red = new Color(255,204,204);
		if (bool) {
			answer_buttons.get(correct_index).setBackground(green);
		} else {
			// Change correct button to green and all other buttons to red
			for (int i = 0; i < answer_options.size(); i++) {
				if (key_word.isEquivalent(answer_options.get(i))) {
					answer_buttons.get(i).setBackground(green);
				} else {
					answer_buttons.get(i).setBackground(red);
				}
			} 
		}
	}
	
	/* Function that handles finding a key word and answer choices, updating the frame, and starting the timer */
	public void start() {
		// Thread to handle the progress bar
		progressThread = new Thread(new Runnable() {
			@Override
			public void run() {
				startTimer();
			}
		});
		// Checking if first run, sleep if not to allow correct answer to be seen
		if (init == false) {
			init = true;
		} else {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Reseting button border to black
		for (JButton btn : answer_buttons) {
			btn.setBackground(new Color(246, 246, 246));
		}

		// Getting quiz word + 4 other possible words
		key_word = Manager.getKeyWord();
		Word correct_answer = Manager.getEquivalent(key_word);
		answer_options.clear();
		while (answer_options.size() != 3) {
			Word option = Manager.getAnswer();
			if (answer_options.contains(option) || option.equals(correct_answer)) {
				continue;
			}
			answer_options.add(option);
		}
		// Adding correct answer to random index
		Random random = new Random();
		answer_options.add(random.nextInt(4), correct_answer);
		// Populating label and buttons
		current_word.setText(key_word.toString());
		for (int i  = 0; i < answer_buttons.size(); i++) {
			answer_buttons.get(i).setText(answer_options.get(i).toString());
		}
		// Starting timer
		wait = false;
		progressThread.start();
	}

	/* Function that handles incrementing the progress bar*/
	public void startTimer() {
		int i = 5000;
		try {
			Thread.sleep(1);
			while (i >= 0) {
				// If button is clicked leave loop and stop timer
				if (button_clicked) {
					break;
				}
				if (bar.getValue() > i) {
					return;
				}
				bar.setValue(i - 10);
				// delay the thread
				Thread.sleep(12);
				i -= 10;
			}
			// If button not clicked call timeout with predefined values
			if (!button_clicked) {
				bar.setValue(5000);
				bar.setValue(0);
				timeOut(false, -1);
			} else { // If button was clicked call timeout with global instances that were updated by button click event
				timeOut(is_correct, correct_index);
			}
		} catch (Exception e) {
		}
	}
	
	/* Function that gets called when timer ends (time runs out or button is clicked)
	 * @param bool -> true if correct answer clicked, false otherwise
	 * @param index -> index of correct answer button, -1 if incorrect button clicked
	 */
	public void timeOut(Boolean bool, Integer index) {
		// Remove one life (heart) and start next round or stop if no lives remaining
		if (!bool) {
			System.out.println("timeOut - Lose one life");
			for (int i = 2; i >= 0; i--) {
				if (hearts.get(i).isVisible()) {
					hearts.get(i).setVisible(false);
					break;
				}
			}
		}
		evaluateSelection(bool, index);
		if (!hearts.get(0).isVisible()) {
			stop(1500);
		} else {
			button_clicked = false;
			start();
		}
	}
	
	/* Function that handles resetting instance variables and frame when no lives remain or stop pressed
	 * @param delay -> If stop called due to lack of lives, correct answer is shown with thread sleeping for "delay" amount of time
	 * otherwise if stop button is pressed, stop is called with delay 0
	 */
	public void stop(Integer delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Reset hearts, labels, and buttons
		bar.setValue(0);
		for (JButton btn : answer_buttons) {
			btn.setText("- - -");
			btn.setBackground(new Color(246, 246, 246));
		}
		for (JLabel heart : hearts) {
			heart.setVisible(true);
		}
		start_button.setText("Start");
		current_word.setText("Click Start");
		// Reset instance variables
		key_word = null;
		wait = true;
		init = false;
		button_clicked = false;
		is_correct = false;
		correct_index = -1;
		answer_options.clear();
	}
	

	public static void main(String[] args) {
		new MyFrame();
	};

}
