package com.sahabatquran.webapp.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.Duration;

public class SeleniumContainerFactory {
    
    private static BrowserWebDriverContainer<?> container;
    
    public static synchronized WebDriver createWebDriver() {
        if (container == null) {
            container = createSeleniumContainer();
            container.start();
            
            // Display VNC URL and pause if VNC viewer is enabled
            String vncEnabled = System.getProperty("selenium.debug.vnc.enabled", "false");
            if ("true".equalsIgnoreCase(vncEnabled)) {
                String vncUrl = container.getVncAddress();
                System.out.println("🔍 VNC Viewer Available at: " + vncUrl);
                System.out.println("📋 Copy the URL above to connect with VNC viewer for debugging");
                
                // Pause to allow user to copy VNC URL
                String pauseSecondsStr = System.getProperty("selenium.debug.vnc.pause.seconds", "5");
                int pauseSeconds = Integer.parseInt(pauseSecondsStr);
                System.out.println("⏸️  Pausing for " + pauseSeconds + " seconds to allow VNC setup...");
                
                try {
                    Thread.sleep(pauseSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("▶️  Continuing with test execution...");
            }
        }
        
        ChromeOptions options = createChromeOptions();
        return new RemoteWebDriver(container.getSeleniumAddress(), options);
    }
    
    private static BrowserWebDriverContainer<?> createSeleniumContainer() {
        String arch = System.getProperty("os.arch").toLowerCase();
        DockerImageName imageName;
        
        if (arch.contains("aarch64") || arch.contains("arm64")) {
            // ARM64 architecture (Apple M1/M2)
            imageName = DockerImageName.parse("seleniarm/standalone-chromium:latest")
                    .asCompatibleSubstituteFor("selenium/standalone-chrome");
        } else {
            // x86_64 architecture
            imageName = DockerImageName.parse("selenium/standalone-chrome:4.15.0");
        }
        
        BrowserWebDriverContainer<?> seleniumContainer = new BrowserWebDriverContainer<>(imageName);
        
        // Optimize container performance
        seleniumContainer
                .withSharedMemorySize(1024 * 1024 * 1024L) // 1GB shared memory
                .withStartupTimeout(Duration.ofSeconds(30))
                .withAccessToHost(true)
                .withExtraHost("host.testcontainers.internal", "host-gateway");
        
        // Configure JVM options for better performance
        seleniumContainer.withEnv("JAVA_OPTS", "-Xmx768m -XX:+UseContainerSupport");
        
        // Check debug modes and log once
        String vncEnabled = System.getProperty("selenium.debug.vnc.enabled", "false");
        String recordingEnabled = System.getProperty("selenium.debug.recording.enabled", "false");
        
        if ("true".equalsIgnoreCase(vncEnabled) || "true".equalsIgnoreCase(recordingEnabled)) {
            System.out.println("🔍 VNC/Recording enabled - automatically disabling headless mode");
        }
        
        // Configure recording based on enabled features
        if ("true".equalsIgnoreCase(recordingEnabled)) {
            // Create timestamped recording directory
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            );
            File recordingDir = new File(System.getProperty("user.dir") + "/target/selenium-recordings/" + timestamp);
            if (!recordingDir.exists()) {
                recordingDir.mkdirs();
            }
            
            // Try Testcontainers recording first
            try {
                seleniumContainer.withRecordingMode(
                    BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL,
                    recordingDir
                );
                System.out.println("📹 Testcontainers recording enabled - videos will be saved to: " + recordingDir.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("⚠️  Testcontainers recording failed: " + e.getMessage());
                System.out.println("💡 Note: Recording may not work with all Docker images. VNC viewer is still available.");
            }
            
            // Additional logging for debugging
            System.out.println("📂 Recording directory: " + recordingDir.getAbsolutePath());
        }
        
        return seleniumContainer;
    }
    
    private static ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        // Session isolation
        options.addArguments("--incognito");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        // Check if VNC or recording is enabled - they require headless to be false
        String vncEnabled = System.getProperty("selenium.debug.vnc.enabled", "false");
        String recordingEnabled = System.getProperty("selenium.debug.recording.enabled", "false");
        
        // Check if headless mode is explicitly requested (default: true, unless VNC/recording enabled)
        String headless = System.getProperty("selenium.headless", "true");
        
        // Automatically disable headless if VNC or recording is enabled
        if ("true".equalsIgnoreCase(vncEnabled) || "true".equalsIgnoreCase(recordingEnabled)) {
            // Don't add --headless argument (logging is done once during container creation)
        } else if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless");
        }
        
        // Performance optimizations
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1080");
        
        return options;
    }
    
    public static synchronized void cleanup() {
        if (container != null && container.isRunning()) {
            System.out.println("🛑 Stopping Selenium container and finalizing recordings...");
            container.stop();
            container = null;
            System.out.println("✅ Container stopped successfully");
        }
    }
}