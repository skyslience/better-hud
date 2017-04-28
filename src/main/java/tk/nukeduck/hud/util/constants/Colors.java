package tk.nukeduck.hud.util.constants;

public class Colors {
	public static final int WHITE       = fromRGB(255, 255, 255);
	public static final int GRAY        = fromRGB(63, 63, 63);
	public static final int BLACK       = fromRGB(0, 0, 0);
	public static final int TRANSLUCENT = fromARGB(85, 0, 0, 0);
	public static final int RED         = fromRGB(255, 0, 0);
	public static final int GREEN       = fromRGB(0, 255, 0);
	
	public static int fromRGB(int r, int g, int b) {
		return fromARGB(255, r, g, b);
	}
	
	public static int fromARGB(int a, int r, int g, int b) {
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}
}
