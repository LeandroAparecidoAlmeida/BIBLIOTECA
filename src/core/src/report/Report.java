package report;

import java.sql.Connection;
import java.sql.SQLException;
import java.awt.Desktop;
import java.awt.Dialog.ModalExclusionType;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JasperExportManager;
import database.FBManager;
import environment.Config;

/**
 * Classe para a exibição do relatório do Jasper Reports. O arquivo de layout
 * de relatório com a extensão .jasper deve estar compilado e incorporado no 
 * arquivo .jar do projeto.
 */
public final class Report {
    
    /**Lista dos parâmetros para a exibição do relatório.*/
    private final HashMap<String, Object> hashMap;
    /**Arquivo .jasper incorporado ao jar do projeto.*/
    private final String layoutFile; 

    /**
     * Criar uma instância de Relatorio.
     * @param layoutFile arquivo .jasper incorporado ao jar do projeto.
     */
    public Report(String layoutFile) throws IOException, SQLException {
        this.layoutFile = layoutFile;
        hashMap = new HashMap<>();
    }
    
    /**
     * Definir o valor de um parâmetro para a geração do relatório.
     * @param name nome do parâmetro.
     * @param value valor do parâmetro.
     */
    public final void putParameter(String name, Object value) {
        hashMap.put(name, value);        
    }

    /**
     * Exibir o relatório no visualizador padrão do Jasper Reports.
     * @param title título na barra de títulos do visualizador do Jasper Reports. 
     */
    private void printDefault(String title) throws JRException, SQLException  {    
        InputStream istream = getClass().getResourceAsStream(layoutFile);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(istream);
        try (Connection datasource = FBManager.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                hashMap,
                datasource
            );
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle(title);
            jasperViewer.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
            Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getMaximumWindowBounds().getBounds();
            jasperViewer.setBounds(r);
            jasperViewer.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            jasperViewer.setIconImage(new ImageIcon(getClass()
            .getResource("/icons32/icon01.png")).getImage());
            jasperViewer.setVisible(true);
        }
    }
    
    /**
     * Exportar o relatório para PDF.
     * @param title título do relatório.
     * @return arquivo PDF gerado.
     */
    private File printPdf(String title) throws JRException, IOException, SQLException {
        InputStream istream = getClass().getResourceAsStream(layoutFile);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(istream);
        File pdfFile;
        try (Connection datasource = FBManager.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                hashMap,
                datasource
            );
            File tempDir = new File(System.getProperty("cachedir"));
            //Formata a data corrente como parte do nome do arquivo.
            String sufix = String.format("-%1$td%1$tm%1$tY%1$tH%1$tM%1$tS", new Date());
            String path = tempDir.getAbsolutePath() + "//" + title + sufix + ".pdf";
            pdfFile = new File(path);
            try (FileOutputStream fostream = new FileOutputStream(pdfFile)) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, fostream);
            }
        }
        return pdfFile;
    }
    
    /**
     * Exportar o relatório para HTML.
     * @param title título do relatório.
     * @return arquivo HTML gerado.
     */
    private File printHTML(String title) throws JRException, IOException, SQLException {
        InputStream istream = getClass().getResourceAsStream(layoutFile);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(istream);
        File htmlFile;
        try (Connection datasource = FBManager.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                hashMap,
                datasource
            );
            File tempDir = new File(System.getProperty("cachedir"));
            //Formata a data corrente como parte do nome do arquivo.
            String sufix = String.format("-%1$td%1$tm%1$tY%1$tH%1$tM%1$tS", new Date());
            String path = tempDir.getAbsolutePath() + "//" + title + sufix + ".html";
            htmlFile = new File(path);
            JasperExportManager.exportReportToHtmlFile(
                jasperPrint,
                htmlFile.getAbsolutePath()
            );
        }
        return htmlFile;
    }
    
    /**
     * Imprimir o relatório de acordo com a opção configurada pelo usuário.
     * @param title título do relatório.
     */
    public void print(String title) throws SQLException, JRException,
    IOException {
        int option = Config.getInteger("report_type");
        switch (option) {
            case 1: {
                printDefault(title);
            } break;
            case 2: {
                File pdfFile = printPdf(title);
                Desktop.getDesktop().open(pdfFile);
            } break;
            case 3: {
                File htmlFile = printHTML(title);
                Desktop.getDesktop().open(htmlFile);
            }
        }
    }
    
}