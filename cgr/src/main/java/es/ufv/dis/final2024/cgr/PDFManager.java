package es.ufv.dis.final2024.cgr;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PDFManager {

    public void createPdf(DataEntry data) throws DocumentException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("./codigos/" + data.getMscode() + ".pdf"));
            document.open();
            // Añadir un párrafo por cada pieza de información en DataEntry
            document.add(new Paragraph("MS Code: " + data.getMscode()));
            document.add(new Paragraph("Year: " + data.getYear()));
            document.add(new Paragraph("Est. Code: " + data.getEstCode()));
            document.add(new Paragraph("Estimate: " + data.getEstimate()));
            document.add(new Paragraph("SE: " + data.getSe()));
            document.add(new Paragraph("Lower CIB: " + data.getLowerCIB()));
            document.add(new Paragraph("Upper CIB: " + data.getUpperCIB()));
            document.add(new Paragraph("Flag: " + data.getFlag()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}



