package utils;

import java.io.File;

/**
 * Classe responsável pela remoção dos arquivos temporários gravados no diretório
 * cache do sistema. Para os formatos de relatório PDF e HTML, os arquivos serão
 * criados no diretório cache.
 * @author Leandro Aparecido de Almeida
 */
public class CacheCleaner {
    
    /**
     * Excluir todos os arquivos e subdiretórios que estão dentro de um
     * diretório.
     * @param dir diretório a ter os arquivos e subdiretório excluídos. 
     */
    private static void cleanDir(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                cleanDir(file);
            }
            file.delete();
        }
    }
    
    /**
     * Limpar o diretório cache do sistema.
     */
    public static void execute() {
        File tempDir = new File(System.getProperty("cachedir"));
        if (tempDir.exists()) {
            cleanDir(tempDir);
        }
    }
    
}
