package com.bookstore.utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class PDFGenerator {
	
	public void generateItenerary(Order order, String filepath) {
		
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(filepath));
			
			// Opened the document and can start working on the document now.
			document.open();
			
			Font font = FontFactory.getFont("Helvetica", Font.BOLD);
			
			PdfPTable table = createBoxInsertTable();
			
			for(int i=0; i<order.getCartItemList().size(); i++) {
				createBoxInsertTableRow(table, order.getCartItemList().get(i));
			}
			
			table.setFooterRows(3);
			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph("Subtotal", font)));
			table.addCell(new PdfPCell(new Paragraph(order.getOrderTotal().toString())));
			
			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph("TOTAL", font)));
			table.addCell(new PdfPCell(new Paragraph("==================")));

			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph()));
			table.addCell(new PdfPCell(new Paragraph("Tax", font)));
			table.addCell(new PdfPCell(new Paragraph("***********")));

			document.add(table);
			
			document.close();
			
			
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private void createBoxInsertTableRow(PdfPTable table, CartItem cartItem) {
		
		table.addCell(new PdfPCell(new Paragraph(cartItem.getBook().getTitle().toString())));
		table.addCell(new PdfPCell(new Phrase(new BigDecimal(cartItem.getBook().getOurPrice()).toString())));
		table.addCell(new PdfPCell(new Paragraph(new Phrase(cartItem.getQty()).toString())));
		table.addCell(new PdfPCell(new Paragraph(cartItem.getSubTotal().toString())));
		
	}

	private PdfPTable createBoxInsertTable() {
		
		Font font = FontFactory.getFont("Helvetica", Font.BOLD);
		
		//Creating table with 5 columns
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		
		PdfPCell h1 = new PdfPCell(new Paragraph("Item Name", font));
		h1.setExtraParagraphSpace(10f);
		
		PdfPCell h2 = new PdfPCell(new Paragraph("Item Price", font));
		PdfPCell h3 = new PdfPCell(new Paragraph("Item Quantity", font));
		PdfPCell h4 = new PdfPCell(new Paragraph("Total", font));
		
		table.setHeaderRows(1);
		
		table.addCell(h1);
		table.addCell(h2);
		table.addCell(h3);
		table.addCell(h4);
		
		return table;
	}

}
