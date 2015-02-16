package com.scheidt.quickflix.batch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by NewRob on 2/8/2015.
 */
public class ImageHelper {

    public static final String NO_IMG = "/noMovieImg.jpg";

    public static BufferedImage getSizedImage(final URL url, final Dimension size) throws IOException {
        final BufferedImage image = ImageIO.read(url);
        final BufferedImage bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return bufferedImage;
    }

    public static BufferedImage getImage(final URL url) throws IOException {
        final BufferedImage image = ImageIO.read(url);
        return image;
    }
}
