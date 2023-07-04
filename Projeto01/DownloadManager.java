package Projeto01;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DownloadManager {
    private static final int NUM_THREADS = 3;
    private static final int TIMEOUT_SECONDS = 10;
    private static ExecutorService executorService;
    private static List<Future<?>> downloadFutures;
    public static void main(String[] args) { 
        String[] urls = {
            "https://centroespiritachicoxavier.org.br/livros/144.pdf",
            "http://www.ep.com.br/livros_vest/vidas_secas.pdf",
             "http://ipv4.download.thinkbroadband.com/10MB.zip"
        };

        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        downloadFutures = new ArrayList<>();

        for (String url : urls) {
            Future<?> future = executorService.submit(() -> {
                try {
                    downloadFile(url);
                } catch (IOException e) {
                    System.out.println("Failed to download file from " + url);
                }
            });
            downloadFutures.add(future);
        }

        // Espera a conclusão de todos os downloads ou até que o tempo limite seja atingido
        try {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for downloads to complete");
        }

        // Cancela downloads pendentes, se houver
        for (Future<?> future : downloadFutures) {
            future.cancel(true);
        }
    }

        
        private static String getFileName(String url) {
            int lastSlashIndex = url.lastIndexOf('/');
            return url.substring(lastSlashIndex + 1);
        }

        private static long getFileSize(URL url) throws IOException {
            return url.openConnection().getContentLengthLong();
        }

        private static void displayProgress(String fileName, long downloadedSize, long fileSize) {
            double progress = (double) downloadedSize / fileSize * 100;
            System.out.printf("%s - %.2f%%\n", fileName, progress);
        }

        
        private static void downloadFile(String url) throws IOException {
            String fileName = getFileName(url);
            BufferedInputStream in = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL fileUrl = new URL(url);
                in = new BufferedInputStream(fileUrl.openStream());
                fileOutputStream = new FileOutputStream(fileName);

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                long fileSize = getFileSize(fileUrl);
                long downloadedSize = 0;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    downloadedSize += bytesRead;
                    displayProgress(fileName, downloadedSize, fileSize);

                    // Verifica se o download foi cancelado
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Download canceled: " + fileName);
                        break;
                    }
                }

                if (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Download complete: " + fileName);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
        }
    }

