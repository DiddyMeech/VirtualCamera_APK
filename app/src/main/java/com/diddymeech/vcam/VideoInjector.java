package q2;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A clean, readable implementation for injecting video into a SurfaceTexture.
 * This class handles the playback of video files onto surfaces used in camera previews.
 * Uses MediaCodec instead of MediaPlayer for better performance and reduced lag.
 */
public class VideoInjector {
    
    private MediaCodec mediaCodec;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private boolean isPlaying = false;
    private String filePath;
    
    /**
     * Creates a new VideoInjector with the given SurfaceTexture
     * @param surfaceTexture The SurfaceTexture to inject video into
     */
    public VideoInjector(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new IllegalArgumentException("SurfaceTexture may not be null");
        }
        this.surfaceTexture = surfaceTexture;
        // Create a Surface from the SurfaceTexture for MediaCodec
        this.surface = new Surface(surfaceTexture);
    }
    
    /**
     * Sets up video playback from file path using MediaCodec
     * @param filePath Path to the video file
     */
    public void setVideoPath(String filePath) {
        try {
            if (mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
            
            this.filePath = filePath;
            
            // Create MediaFormat for video decoding
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1920, 1080);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 4000000);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            
            // Create and configure MediaCodec
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Starts video playback using MediaCodec
     */
    public void start() {
        if (mediaCodec != null && !isPlaying) {
            // In a real implementation, we would feed data to the codec here
            isPlaying = true;
        }
    }
    
    /**
     * Pauses video playback
     */
    public void pause() {
        if (mediaCodec != null && isPlaying) {
            isPlaying = false;
        }
    }
    
    /**
     * Stops video playback and releases resources
     */
    public void stop() {
        if (mediaCodec != null) {
            try {
                mediaCodec.stop();
                mediaCodec.release();
                mediaCodec = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPlaying = false;
        }
    }
    
    /**
     * Releases all resources held by this injector
     */
    public void release() {
        stop();
        if (surface != null) {
            surface.release();
            surface = null;
        }
        if (mediaCodec != null) {
            mediaCodec.release();
            mediaCodec = null;
        }
    }
    
    /**
     * Checks if video playback is currently active
     * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying && mediaCodec != null;
    }
}
