package flume;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.interceptor.Interceptor;

public class CustomInterceptor implements Interceptor {

	private String hostValue;
	private String hostHeader;

	public CustomInterceptor(String hostHeader) {
		this.hostHeader = hostHeader;
	}

	@Override
	public void close() {

	}

	@Override
	public void initialize() {

		try {
			hostValue = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new FlumeException("Cannot get Hostname", e);
		}

	}

	@Override
	public Event intercept(Event input) {
		byte[] data = input.getBody();
		Map<String, String> headers = input.getHeaders();
		headers.put(hostHeader, hostValue);

		byte[] modifyData = "Flume Interceptor".getBytes();

		input.setBody(modifyData);
		return input;
	}

	@Override
	public List<Event> intercept(List<Event> inputs) {

		for (Event input : inputs) {
			intercept(input);
		}
		return inputs;
	}

	public static class Builder implements Interceptor.Builder {
		private String hostHeader;

		@Override
		public void configure(Context context) {
			hostHeader = context.getString("hostHeader");
		}

		@Override
		public Interceptor build() {
			return new CustomInterceptor(hostHeader);
		}

	}

}
