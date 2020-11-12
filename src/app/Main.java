package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import app.control.Log;
import app.view.MainFrame;

public class Main {

	private static void createAndShowGUI() {

		JFrame frame = new MainFrame();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();

				Log.getInstance().onGlobalProgress$()
						.doOnNext(progress -> System.out.println(progress.label + ": " + progress.state.toString()
								+ " (" + progress.percent + ")"))
						.subscribe();

			}
		});

	}

}
