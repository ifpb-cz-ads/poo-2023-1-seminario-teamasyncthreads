import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GerenciadorDownload {
    private static final int NUM_THREADS = 3;
    private static final int TIMEOUT_SEGUNDOS = 10;

    private static ExecutorService executorService;
    private static List<Future<?>> futuresDownloads;

    public static void main(String[] args) {
        String[] urls = {
                "https://centroespiritachicoxavier.org.br/livros/144.pdf",
                "http://www.ep.com.br/livros_vest/vidas_secas.pdf",
                "http://ipv4.download.thinkbroadband.com/10MB.zip"
        };

        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        futuresDownloads = new ArrayList<>();

        for (String url : urls) {
            Future<?> future = executorService.submit(() -> {
                try {
                    baixarArquivo(url);
                } catch (IOException e) {
                    System.out.println("Falha ao baixar o arquivo de " + url);
                }
            });
            futuresDownloads.add(future);
        }

        // Aguarda a conclusão de todos os downloads ou até que o tempo limite seja atingido
        try {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrompido enquanto aguardava a conclusão dos downloads");
        }

        // Cancela downloads pendentes, se houver
        for (Future<?> future : futuresDownloads) {
            future.cancel(true);
        }
    }

    private static void baixarArquivo(String url) throws IOException {
        String nomeArquivo = obterNomeArquivo(url);
        BufferedInputStream in = null;
        FileOutputStream fileOutputStream = null;

        try {
            URL fileUrl = new URL(url);
            in = new BufferedInputStream(fileUrl.openStream());
            fileOutputStream = new FileOutputStream(nomeArquivo);

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            long tamanhoArquivo = obterTamanhoArquivo(fileUrl);
            long tamanhoBaixado = 0;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                tamanhoBaixado += bytesRead;
                exibirProgresso(nomeArquivo, tamanhoBaixado, tamanhoArquivo);

                // Verifica se o download foi cancelado
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Download cancelado: " + nomeArquivo);
                    break;
                }
            }

            if (!Thread.currentThread().isInterrupted()) {
                System.out.println("Download concluído: " + nomeArquivo);
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

    private static String obterNomeArquivo(String url) {
        int ultimaBarraIndex = url.lastIndexOf('/');
        return url.substring(ultimaBarraIndex + 1);
    }

    private static long obterTamanhoArquivo(URL url) throws IOException {
        return url.openConnection().getContentLengthLong();
    }

    private static void exibirProgresso(String nomeArquivo, long tamanhoBaixado, long tamanhoArquivo) {
        double progresso = (double) tamanhoBaixado / tamanhoArquivo * 100;
        System.out.printf("%s - %.2f%%\n", nomeArquivo, progresso);
    }
}