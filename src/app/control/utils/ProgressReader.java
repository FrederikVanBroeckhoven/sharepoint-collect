package app.control.utils;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class ProgressReader extends FilterReader {

	private Subject<Long> onProgress$;
	
	public ProgressReader(Reader in) {
		super(in);
		onProgress$ = BehaviorSubject.createDefault(0l);
	}

	@Override
	public int read() throws IOException {
		int i = super.read();
		onProgress$.onNext(Long.valueOf(i));
		return i;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int i = super.read(cbuf, off, len);
		onProgress$.onNext(Long.valueOf(i));
		return i;
	}

	@Override
	public long skip(long n) throws IOException {
		long l = super.skip(n);
		onProgress$.onNext(l);
		return l;
	}

	@Override
	public void close() throws IOException {
		super.close();
		onProgress$.onComplete();
	}
	
	public Subject<Long> onProgress$() {
		return onProgress$;
	}
	
}
