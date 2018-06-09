
public class Logger {
	public static void log(Exception e) {
		String stacktraceStr = "";
		StackTraceElement[] stacktrace = e.getStackTrace();
		for(StackTraceElement element : stacktrace) {
			stacktraceStr+=element.toString()+"\n";
		}
		BotUtils.sendMePm("help: "+e.getMessage()+"\n\n"+stacktraceStr);

		System.err.println("Operation could not be completed with error: "+e.getMessage()+"\n\n");
		e.printStackTrace();
	}
}
