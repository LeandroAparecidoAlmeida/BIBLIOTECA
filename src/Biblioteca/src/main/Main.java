package main;

import java.io.File;
import javax.swing.UIManager;
import dialogs.ErrorDialog;
import gui.dialogs.MainWindow;
import gui.dialogs.SplashScreen;
import utils.CacheCleaner;

/**
 * Classe de inicialização do programa.
 * @author Leandro Aparecido de Almeida
 */
public class Main {

    /**
     * Método main da aplicação Java.
     * @param args (não espera parâmetros externos). 
     */
    public static void main(String[] args) {        
        try {
            //Programa assume a aparência dos programas nativos. 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //Configura as propriedades do sistema.
            String rootDir = new File(Main.class.getProtectionDomain().getCodeSource()
            .getLocation().toURI()).getParent() + File.separator;
            String cacheDir = rootDir + "cache" + File.separator;
            System.setProperty("rootdir", rootDir);  
            System.setProperty("cachedir", cacheDir);  
            File tempDir = new File(cacheDir);
            if (!tempDir.exists()) tempDir.mkdir();
            //Configura para que ao fechar o programa, limpe o diretório cache.
            Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {                            
                        CacheCleaner.execute();
                    }
                }
            );
            //Tela de apresentação é exibida por 6 segundos. Em seguida, abre
            //a tela principal.
            new SplashScreen(null, 6000).setVisible(true);
            new MainWindow().setVisible(true);
        } catch (Exception ex) {
            ErrorDialog.showException(
                null,
                "Inicialização abortada!",
                ex
            );
            System.exit(1);
        }
    }
    
}