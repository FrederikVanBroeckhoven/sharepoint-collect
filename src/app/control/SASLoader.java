package app.control;

import java.io.File;
import java.io.FileReader;

import org.javatuples.Pair;

import com.google.gson.Gson;

import app.control.utils.Progress;
import app.control.utils.ProgressReader;
import app.model.SASItem;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class SASLoader {

	private static SASLoader sasLoader;

	public static SASLoader getInstance() {
		if (sasLoader == null) {
			sasLoader = new SASLoader();
		}
		return sasLoader;
	}

	private Gson gson;

	private Subject<File> onSASFileLoad$;
	private Subject<SASItem[]> onSASItemsLoad$;
	private Subject<Boolean> onSASFileLoaded$;

	private SASLoader() {
		init();
	}

	private void init() {
		gson = new Gson();

		onSASFileLoad$ = BehaviorSubject.create();
		onSASItemsLoad$ = BehaviorSubject.create();
		onSASFileLoaded$ = BehaviorSubject.createDefault(Boolean.FALSE);

		onSASFileLoad$
				// it's convenient to have both the file and reader in the next steps,
				// so wrap both in a pair
				.map(file -> Pair.with(file, new ProgressReader(new FileReader(file))))
				// initialize the global load progress
				.doOnNext(p -> {
					Log.getInstance().globalProgress(Progress.init(p.getValue0().getName()));
					onSASFileLoaded$.onNext(Boolean.FALSE);								
				})
				// subscribe to the reader's progress fed into the global load progress
				.doOnNext(p -> p.getValue1().onProgress$()
						.filter(l -> l > 0)
						.doOnNext(l -> Log.getInstance().globalProgress(Progress.progress(p.getValue0().getName(), l, p
								.getValue0().length())))
						.subscribe())
				// the following (inner) observable reads the json, and handles possible errors
				.switchMap(p -> {
					return Observable.just(p.getValue1())
							// read the json
							.map(reader -> {
								SASItem[] items = gson.fromJson(reader, SASItem[].class);
								Log.getInstance().globalProgress(Progress.done(
									items.length + " records loaded!"
								));
								onSASFileLoaded$.onNext(Boolean.TRUE);
								return items;
							})
							// report the errors (if any) and use the filename in the error message
							// and notify the global load progress
							.doOnError(e -> {
								Log.getInstance().globalProgress(Progress.fail("load failed!"));
								Log.getInstance().userError("Sorry, can't open '" + p.getValue0()
										.getAbsolutePath() + "'");
								onSASFileLoaded$.onNext(Boolean.FALSE);
							})
							// make sure the error completes the (inner) observable
							.onErrorReturnItem(new SASItem[] {})
							// close the reader on completion (and on error)
							.doOnComplete(() -> p.getValue1().close());
				})
				.doOnNext(sas -> {
					onSASItemsLoad$.onNext(sas);
				})
				.subscribe();

	}

	public void load(File sasFile) {
		onSASFileLoad$.onNext(sasFile);
	}

	public Observable<File> onSASFileLoad$() {
		return onSASFileLoad$;
	}
	
	public Observable<SASItem[]> onSASItemsLoad$() {
		return onSASItemsLoad$;
	}

	public Observable<Boolean> onSASItemsLoaded$() {
		return onSASFileLoaded$;
	}

}
