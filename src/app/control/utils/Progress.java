package app.control.utils;

public class Progress {

	public enum State {
		PROGRESS,
		DONE,
		FAIL,
		WAITING
	}

	public String label;
	public double percent;
	public State state;

	private Progress(String label, double percent, State state) {
		this.label = label;
		this.percent = percent;
		this.state = state;
	}

	public static Progress fail(String label) {
		return new Progress(label, 0, State.FAIL);
	}

	public static Progress done(String label) {
		return new Progress(label, 1, State.DONE);
	}

	public static Progress progress(String label, double p) {
		return new Progress(label, p, State.PROGRESS);
	}

	public static Progress progress(String label, long curr, long max) {
		return progress(label, (double) curr / max);
	}

	public static Progress init(String label) {
		return progress(label, 0);
	}

	public static Progress waiting(String label) {
		return new Progress(label, 0, State.WAITING);
	}

}
