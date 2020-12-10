package app.control;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

import app.control.SharePointConnection.State;
import app.model.SASItem;
import dashboard.connect.Item;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SharePointAccess {

	private static SharePointAccess instance;

	public static final SharePointAccess getInstance() {
		if (instance == null) {
			instance = new SharePointAccess();
		}
		return instance;

	}

	private Map<URL, SharePointConnection> connections;

	private PublishSubject<SharePointConnection> onStates$;

	private SharePointAccess() {
		connections = new HashMap<URL, SharePointConnection>();
		onStates$ = PublishSubject.create();

		init();
	}

	private void init() {
	}

	public SharePointConnection setupEndpoint(URL endpoint) {
		SharePointConnection cred = connections.get(endpoint);
		if (cred == null) {
			cred = new SharePointConnection(endpoint);
			connections.put(endpoint, cred);
			onStates$.onNext(cred);
		}
		return cred;
	}

	public void connect(String username, char[] password) {
		Observable.defer(
				() -> Observable.fromIterable(connections.entrySet())
						.map(entry -> entry.getValue())
						.filter(connection -> connection.state == SharePointConnection.State.INIT)
						.distinct()
						.switchMap(
								connection -> Observable.just(connection)
										.doOnNext(
												innerConn -> {
													updateStatus(innerConn, SharePointConnection.State.WAITING);
													innerConn.source.getCredentials().aquire(username, new String(
															password));
													updateStatus(innerConn, SharePointConnection.State.CONNECTED);
												})
										.doOnError(e -> updateStatus(connection, SharePointConnection.State.FAIL))
										.onErrorReturnItem(connection)
										.onErrorComplete()))
				.subscribe();
	}

	public void disconnect() {
		connections.clear();
	}

	public void list(SASItem item) {
		Observable.defer(
				() -> Observable.just(item.getListLocation())
						.switchMap(
								loc -> {

									URL url = loc.url;
									String title = loc.name;

									return Observable.just(Pair.with(connections.get(url), title))
											.map(locator -> Pair.with(
													locator.getValue0().source,
													locator.getValue1()))
											.map(locator -> locator.getValue0().listItems())
											.onErrorComplete();
								})
						.doOnNext(res -> {
							SASManager.getInstance().SASItemListed(item, res);
						}))
				.subscribe();
	}

	public void listAttachments(SASItem sas, Item item) {
		Observable.defer(
				() -> Observable.just(sas.getListLocation())
						.switchMap(
								loc -> {
									URL url = loc.url;
									String title = loc.name;

									return Observable.just(Pair.with(connections.get(url), title))
											.map(locator -> Pair.with(
													locator.getValue0().source,
													locator.getValue1()))
											.map(locator -> locator.getValue0().listAttachments(item))
											.onErrorComplete();
								})
						.doOnNext(res -> {
							SASManager.getInstance().attachmentsListed(sas, item, res);
						}))
				.subscribe();
	}

	public SharePointConnection getConnection(URL url) {
		return connections.get(url);
	}

	public Observable<SharePointConnection> onStateChanged$() {
		return onStates$;
	}

	public Observable<SharePointConnection> onStateFor$(URL url) {
		return onStates$.filter(connection -> connection.url.equals(url));
	}

	private void updateStatus(SharePointConnection connection, State state) {
		connection.state = state;
		onStates$.onNext(connection);
	}

}
