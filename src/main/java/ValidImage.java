import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ValidImage {
	private BufferedImage source;
	private BufferedImage validImage;
	private BufferedImage result;
	
	public ValidImage(BufferedImage image) throws IOException {
		this.source = image;
		validImage = ImageIO.read(new File("/remote/TheFungusAlumnus/src/main/resources/valid.png"));
		result = applyStamp(source, validImage);
	}
	
	public BufferedImage getImage() {
		return result;
	}
	
	private BufferedImage applyStamp(BufferedImage base, BufferedImage stamp) {
		if(base.getHeight()!=stamp.getHeight() || base.getWidth() !=stamp.getWidth()) {
			stamp = resize(stamp, base.getWidth(), base.getHeight());
		}
		BufferedImage stamped = new BufferedImage(stamp.getWidth(), stamp.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = stamped.createGraphics();
		graphics2d.drawImage(base, 0, 0, null);
		graphics2d.drawImage(stamp, 0, 0, null);
		graphics2d.dispose();
		return stamped;
	}
	
	private BufferedImage resize(Image image, int width, int height) {
		Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = resized.createGraphics();
		graphics2d.drawImage(scaled, 0, 0, null);
		graphics2d.dispose();
		return resized;
	}

}
