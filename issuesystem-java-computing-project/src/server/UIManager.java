import javax.swing.JFrame;

public class UIManager extends UIBaseManager
{
	public static UIReportManagerWindow reportManagerWindow;
	public static UIChatManagerWindow chatManagerWindow;

	public static void load()
	{
		mainWindow = new UIMainWindow();	
	}
}