package app.control;

import app.control.utils.Progress;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class Log {
	
	private static Log errorLog;
	
	public static Log getInstance() {
		if (errorLog == null) {
			errorLog = new Log();
		}
		return errorLog;
	}

	private Subject<String> onUserError$;
	private Subject<Progress> onGlobalProgress$;
	
	
	private Log() {
		init();
	}

	private void init() {
		onUserError$ = PublishSubject.create();
		
		onGlobalProgress$ = PublishSubject.create();
	}
	
	public Observable<String> onUserError$() {
		return onUserError$;
	}
	
	public void userError(String error) {
		onUserError$.onNext(error);
	}
	
	public Observable<Progress> onGlobalProgress$() {
		return onGlobalProgress$;
	}
	
	public void globalProgress(Progress progress) {
		onGlobalProgress$.onNext(progress);
	}
	
}
