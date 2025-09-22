package de.greenoid.game.isola.gui.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading and managing game tile images.
 */
public class ImageLoader {
    // Map to cache loaded images
    private static final Map<String, Image> imageCache = new HashMap<>();
    
    // Image file paths
    private static final String IMAGE_PATH = "/tiles/128px/";
    
    // Background tile images
    private static final String NORMAL_TILE = "Tropical Island.png";
    private static final String BLOCKED_TILE = "Shark Fin Waves.png";
    private static final String PLAYER1_BASE = "Island Sand Castle Red.png";
    private static final String PLAYER2_BASE = "Island Sand Castle Black.png";
    
    // Player character images
    private static final String PLAYER1_CHARACTER = "Castaway Character Red.png";
    private static final String PLAYER2_CHARACTER = "Castaway Character Black.png";
    private static final String PLAYER1_VICTORY = "Joyful Castaway Victory Red.png";
    private static final String PLAYER2_VICTORY = "Joyful Castaway Victory Black.png";
    
    /**
     * Private constructor to prevent instantiation
     */
    private ImageLoader() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Load an image from the resources folder.
     * 
     * @param imagePath The path to the image file
     * @return The loaded Image, or null if loading failed
     */
    private static Image loadImage(String imagePath) {
        // Check if image is already cached
        if (imageCache.containsKey(imagePath)) {
            return imageCache.get(imagePath);
        }
        
        try {
            URL imageURL = ImageLoader.class.getResource(imagePath);
            if (imageURL != null) {
                Image image = ImageIO.read(imageURL);
                imageCache.put(imagePath, image);
                return image;
            } else {
                System.err.println("Could not find image: " + imagePath);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Could not load image: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the normal tile background image.
     * 
     * @return The normal tile image
     */
    public static Image getNormalTileImage() {
        return loadImage(IMAGE_PATH + NORMAL_TILE);
    }
    
    /**
     * Get the blocked tile background image.
     * 
     * @return The blocked tile image
     */
    public static Image getBlockedTileImage() {
        return loadImage(IMAGE_PATH + BLOCKED_TILE);
    }
    
    /**
     * Get the Player 1 base tile image.
     * 
     * @return The Player 1 base tile image
     */
    public static Image getPlayer1BaseImage() {
        return loadImage(IMAGE_PATH + PLAYER1_BASE);
    }
    
    /**
     * Get the Player 2 base tile image.
     * 
     * @return The Player 2 base tile image
     */
    public static Image getPlayer2BaseImage() {
        return loadImage(IMAGE_PATH + PLAYER2_BASE);
    }
    
    /**
     * Get the Player 1 character image.
     * 
     * @return The Player 1 character image
     */
    public static Image getPlayer1CharacterImage() {
        return loadImage(IMAGE_PATH + PLAYER1_CHARACTER);
    }
    
    /**
     * Get the Player 2 character image.
     * 
     * @return The Player 2 character image
     */
    public static Image getPlayer2CharacterImage() {
        return loadImage(IMAGE_PATH + PLAYER2_CHARACTER);
    }
    
    /**
     * Get the Player 1 victory image.
     * 
     * @return The Player 1 victory image
     */
    public static Image getPlayer1VictoryImage() {
        return loadImage(IMAGE_PATH + PLAYER1_VICTORY);
    }
    
    /**
     * Get the Player 2 victory image.
     * 
     * @return The Player 2 victory image
     */
    public static Image getPlayer2VictoryImage() {
        return loadImage(IMAGE_PATH + PLAYER2_VICTORY);
    }
    
    /**
     * Scale an image to fit the specified dimensions.
     * 
     * @param image The image to scale
     * @param width The target width
     * @param height The target height
     * @return The scaled image, or null if the input image was null
     */
    public static Image scaleImage(Image image, int width, int height) {
        if (image == null) {
            return null;
        }
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}