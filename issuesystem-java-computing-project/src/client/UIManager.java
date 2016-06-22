import javax.swing.JFrame;

public class UIManager extends UIBaseManager
{
	public static UISubmitReportWindow submitReportWindow;
	public static UIReportStatusWindow reportStatusWindow;
	public static UILiveChatWindow liveChatWindow;

	public static void load()
	{
		mainWindow = new UIMainWindow();
	}
}