package com.pdfbox.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pdfbox.domain.dto.PDFTemplate;
import com.pdfbox.domain.dto.*;
import com.pdfbox.web.errors.RestResponse;

@Service
public class PDFGenerator {

	private final static Logger log = LoggerFactory.getLogger(PDFGenerator.class);
	
	//@Value(value="${INBOX}")
	//private String inboxStr;
	
	private static String OUTBOX;
	
	private static final String PDF_TEMPLATE_DIR = "templates/pdf/";
	private static DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Value(value="${OUTBOX}")
	public void setINBOX(String iNBOX) {
		OUTBOX = iNBOX;
	}
	
	/**
	 * Permet de générer un pdf en se basant sur le template pdfTemplate et l'alimenter par le Map model
	 * 
	 * @param pdfTemplate
	 * @param model
	 * @return
	 */
	public static RestResponse generate(PDFTemplate pdfTemplate, Map<String, String> model){
		RestResponse response = new RestResponse();
		log.info("generate: load PDF template as resource : " + PDF_TEMPLATE_DIR + pdfTemplate.getTemplateName());
		ClassPathResource templateFile = new ClassPathResource(PDF_TEMPLATE_DIR + pdfTemplate.getTemplateName());
		if(!templateFile.exists()){
			log.error("Erreur lors de la lecture du template PDF");
			response.setCode(500);
			response.setMessage("Erreur lors de la lecture du template PDF");
			return response;
		}
		log.info("generate: load PDF template");
		try {
            PDDocument pDDocument = PDDocument.load(templateFile.getInputStream(), MemoryUsageSetting.setupTempFileOnly());
            PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
            pDAcroForm.setCacheFields(false);
            PDField field;
            Set<String> keys = model.keySet();
            log.info("generate: Checking fields");
            if(keys.size() != pDAcroForm.getFields().size()){
            	response.setCode(500);
    			response.setMessage("Le nombre des données soumises (" + keys.size() + 
    					") ne correspond pas à celui du template PDF (" + pDAcroForm.getFields().size() + ")");
    			return response;
            }
            log.info("generate: load values into template");
            for(String key : keys){
            	field = pDAcroForm.getField(key);
            	if(field != null){
            		field.setValue(model.get(key) == null ? "" : model.get(key));
            	}else{
            		response.setCode(500);
        			response.setMessage("Le champs '" + key + "' n'existe pas dans le template PDF");
        			return response;
            	}
            }
            String dateTime = LocalDateTime.now().format(dtFormatter);
            String generatedPDFName = pdfTemplate.getTemplateName() + dateTime + ".pdf";
            log.info("generate: saving pdf into file : " + generatedPDFName);
            File generatedPdfFile = new File(OUTBOX, generatedPDFName);
            pDDocument.save(generatedPdfFile);
            response.setData(generatedPDFName);
            pDDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
		return response;
	}
	
	public static RestResponse fillFormData(PDDocument pDDocument, Map<String, String> model) throws IOException{
		RestResponse response = new RestResponse();
		PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
        pDAcroForm.setCacheFields(false);
        PDField field;
        Set<String> keys = model.keySet();
        log.info("generate: Checking fields");
        if(keys.size() != pDAcroForm.getFields().size()){
        	response.setCode(500);
			response.setMessage("Le nombre des données soumises (" + keys.size() + 
					") ne correspond pas à celui du template PDF (" + pDAcroForm.getFields().size() + ")");
			return response;
        }
        log.info("generate: load values into template");
        for(String key : keys){
        	field = pDAcroForm.getField(key);
        	if(field != null){
        		log.debug("Setting '" + key + "' value");
        		field.setValue(model.get(key) == null ? "" : model.get(key));
        	}else{
        		response.setCode(500);
    			response.setMessage("Le champs '" + key + "' n'existe pas dans le template PDF");
    			return response;
        	}
        }
        return response;
	}
	
	public static RestResponse previewPdf(Map<PDFTemplate, Map<String, String>> pdfData){
		RestResponse response = new RestResponse();
    	if(pdfData != null && !pdfData.isEmpty()){
        	Set<PDFTemplate> pdfFileNames = pdfData.keySet();
        	for(PDFTemplate pdf1 : pdfFileNames){
        		response = generate(pdf1, pdfData.get(pdf1));
        		if(response.getCode() != 0 || response.getData() == null){
        			if(response.getMessage() != null && response.getMessage().isEmpty()){
        				response.setMessage("Erreur lors de la génération du PDF " + pdf1.name());
        			}
        			return response;
        		}else{
        			String pdfName = (String)response.getData();
        			log.debug("Generated PDF file name : " + pdfName);
        			break;
        		}
        	}
        }else{
        	response.setCode(500);
        	response.setMessage("Missing data");
        }
    	return response;
	}
	
	public static void drawTable(PDDocument doc, PDPage page,
			PdfDocument pdfDocument, PdfContent pdfContent) throws IOException {
		TableCell[][] content = (TableCell[][]) pdfContent.getContent();
	    float margin = pdfDocument.getMargin();
	    float y = pdfDocument.getY();
		final int rows = content.length;
	    final int cols = content[0].length;
	    final float rowHeight = pdfDocument.getTableRowHeight();
	    final float tableWidth = page.getMediaBox().getWidth()-(2*margin);
	    float tableHeight = 0;
	    final float colWidth = tableWidth/(float)cols;
	    final float cellMargin= pdfDocument.getTableCellMargin();
		PDPageContentStream contentStream = new PDPageContentStream(doc, doc.getPage(pdfDocument.getCurrentPage()), PDPageContentStream.AppendMode.APPEND, false);
	    contentStream.setFont(pdfDocument.getFont(pdfContent.getFontType()), pdfContent.getPoliceSize());
		float charWidth = (pdfDocument.getFont(pdfContent.getFontType()).getStringWidth("A") / 1000.0f) * pdfContent.getPoliceSize();

		contentStream.setLineWidth(0.5f);
		//get columns width
	    float[] colsWidth = new float[cols];
	    Arrays.fill(colsWidth, colWidth);
	    for (int i = 0; i < cols; i++) {
	        if(content[0][i].getCellWidth() > 0){
	        	colsWidth[i] = (tableWidth / 100) * content[0][i].getCellWidth();
	        }
	    }
	    // init rows height
		float rowsH[] = new float[rows];
		Arrays.fill(rowsH, rowHeight);
	    
	    // add the text & check lines per cell
	    float textx = margin+cellMargin;
	    float texty = y-15;
	    for(int i = 0; i < content.length; i++){
	        for(int j = 0 ; j < content[i].length; j++){
	            String text;
	            if(content[i][j] != null){
	            	text = content[i][j].getText();
	            }else{
	            	continue;
	            }
	            float text_width = (pdfDocument.getFont(pdfContent.getFontType()).getStringWidth(text) / 1000.0f) * pdfContent.getPoliceSize();
	            float cellWidth = colsWidth[j] - (2 * cellMargin);
	            int lineNb = 1;
	            int textWidthTarget = (int)(cellWidth / charWidth);
	            List<String> textLines = new ArrayList<>();
	            if(text_width > cellWidth){
	            	String []textWords = text.split(" ");
		            StringBuffer textLine = new StringBuffer();
	            	for(String word : textWords){
	            		if((textLine.toString().trim().length() + word.length() + 1) <= textWidthTarget){
	            			textLine.append( " " + word);
	            		}else{
	            			if(textLine.length() > 0) {
	        	            	textLines.add(textLine.toString().trim());
	            			}
	            			textLine = new StringBuffer(word);
	            			lineNb++;
	            		}
	            		if(word.equals( textWords[textWords.length - 1])) {
	            			textLines.add(textLine.toString().trim());
	            		}
	            	}
	            	
	            }else{
	            	textLines.add(text);
	            }
	            // update rows height according to the text
	            if(rowsH[i] < (rowHeight * lineNb)){
		        	rowsH[i] = rowHeight * lineNb;
		        }
	            
	            float initTextY = texty;
	            for(String txtLine : textLines){
	            	text_width = (pdfDocument.getFont(pdfContent.getFontType()).getStringWidth(txtLine) / 1000.0f) * pdfContent.getPoliceSize();

		            contentStream.beginText();
		            switch(content[i][j].getAlign()){
			            case TableCell.ALIGNEMENT_CENTER:
			            	contentStream.newLineAtOffset(textx + (colsWidth[j] / 2) - (text_width / 2) - cellMargin ,texty);
			            	break;
			            case TableCell.ALIGNEMENT_RIGHT:
			            	contentStream.newLineAtOffset(textx + colsWidth[j] - text_width -(cellMargin * 2) ,texty);
			            	break;
			            case TableCell.ALIGNEMENT_LEFT:
		            	default:
		            		contentStream.newLineAtOffset(textx ,texty);
		            }
		            if(content[i][j].isHeader()){
		            	contentStream.setFont(pdfDocument.getFontBold(), pdfContent.getPoliceSize());
		            }else{
		            	contentStream.setFont(pdfDocument.getFont(pdfContent.getFontType()), pdfContent.getPoliceSize());
		            }
		            contentStream.showText(txtLine);
		            contentStream.endText();
		            texty-=rowHeight;
	            }
	            texty = initTextY;
	            textx += colsWidth[j];
	        }
	        texty -= rowsH[i];
	        textx = margin+cellMargin;
	    }
	    

	    //draw the rows
	    float nexty = y ;
        contentStream.moveTo(margin,nexty);
        contentStream.lineTo(margin+tableWidth,nexty);
        contentStream.stroke();
	    for (int i = 0; i < rows; i++) {
	        nexty-= rowsH[i];
	        tableHeight += rowsH[i];
	        contentStream.moveTo(margin,nexty);
	        contentStream.lineTo(margin+tableWidth,nexty);
	        contentStream.stroke();
	    }
	    
	    // draw columns
	    float nextx = margin;
    	contentStream.moveTo(nextx,y);
        contentStream.lineTo(nextx,y-tableHeight);
        contentStream.stroke();
	    for (int i = 0; i < cols; i++) {
	        nextx += colsWidth[i];
	    	contentStream.moveTo(nextx,y);
	        contentStream.lineTo(nextx,y-tableHeight);
	        contentStream.stroke();
	    }
	    
	    pdfDocument.setY(texty);
	    contentStream.close();
	}
	
	public static void drawParagraph(PDDocument doc, PDPage page, PdfDocument pdfDocument, PdfContent pdfContent, boolean withFirstLine) throws IOException{
		PDPageContentStream contentStream = new PDPageContentStream(doc, doc.getPage(pdfDocument.getCurrentPage()), PDPageContentStream.AppendMode.APPEND, false);
		ElementContent[][] content = pdfContent.getContent();
		int align = pdfContent.getAlign();
		Color color = pdfContent.getColor();
		contentStream.setFont(pdfDocument.getFont(pdfContent.getFontType()), pdfContent.getPoliceSize());
	    float textx = pdfDocument.getMargin();
	    float texty = pdfDocument.getY();
	    final float rowHeight = 15f;
	    float paragraphTab = 20f;
	    final float textWidth = page.getMediaBox().getWidth() - (2 * pdfDocument.getMargin());
	    float xc = 0;
	    textx += paragraphTab;
	    texty-=rowHeight;
	    if(color != null){
	    	contentStream.setNonStrokingColor(color);
	    }else{
	    	contentStream.setNonStrokingColor(Color.BLACK);
	    }
	    if(!withFirstLine){
	    	paragraphTab = 0f;
	    }
	    if(align == PdfContent.ALIGNEMENT_SIGNATURE){
	    	for(int i = 0; i < content.length; i++){
		        for(int j = 0 ; j < content[i].length; j++){
		        	String text = content[i][j].getText();
		        	float text_width = (pdfDocument.getFont(pdfContent.getFontType()).getStringWidth(text) / 1000.0f) * pdfContent.getPoliceSize();
		        	if(xc < text_width) {
		        		xc = text_width;
		        	}
		        }
		    }
		    if(Float.compare(xc, 0) != 0){
		    	xc = textWidth - (xc / 2);
		    	texty-= (rowHeight * 2);
		    }
	    }else if(align == PdfContent.ALIGNEMENT_CENTER){
		    	xc = (textWidth / 2) + pdfDocument.getMargin();
	    }
	    for(int i = 0; i < content.length; i++){
	        for(int j = 0 ; j < content[i].length; j++){
	            String text = content[i][j].getText();
	            float text_width = (pdfDocument.getFont(pdfContent.getFontType()).getStringWidth(text) / 1000.0f) * pdfContent.getPoliceSize();
	            if(align == PdfContent.ALIGNEMENT_SIGNATURE || align == PdfContent.ALIGNEMENT_CENTER ){
	            	textx = xc - (text_width / 2);
	            	paragraphTab = 0;
	            }else{
		            float free = textWidth - text_width - paragraphTab;
		            float charSpacing;
		            if (free > 0 && (i < (content.length - 1)))
		            {
		                charSpacing = free / (text.length() - 1);
		            }else{
		            	charSpacing = 0;
		            }
		            contentStream.setCharacterSpacing(charSpacing);
	            }
	            contentStream.beginText();
	            contentStream.newLineAtOffset(textx ,texty);
	            contentStream.showText(text);
	            contentStream.endText();
	        }
        	paragraphTab = 0;
	        textx = pdfDocument.getMargin();
	        texty-=rowHeight;
	    }
	    pdfDocument.setY(texty);
		contentStream.close();
	}
	
	public static void write(PDDocument doc, PdfDocument pdfDocument) throws IOException {
		final float rowHeight = pdfDocument.getTableRowHeight();
		float margin = pdfDocument.getMargin();
	    float cellMargin= pdfDocument.getTableCellMargin();
	    int signatureIdx = -1;
	    if(pdfDocument.getPdfContent().size() > 0 && pdfDocument.getPdfContent().get(pdfDocument.getPdfContent().size() -1 ).getAlign() == PdfContent.ALIGNEMENT_SIGNATURE){
	    	signatureIdx = pdfDocument.getPdfContent().size() -1;
	    }
	    int contIdx = -1;
		for(PdfContent cont : pdfDocument.getPdfContent()){
			contIdx++;
		    float charWidth = (pdfDocument.getFont(cont.getFontType()).getStringWidth("A") / 1000.0f) * cont.getPoliceSize();
			if(cont.isTable()){
				float availableHeight = pdfDocument.getY() - pdfDocument.getMargin();
			    final int cols = cont.getContent()[0].length;
			    final float tableWidth = doc.getPage(pdfDocument.getCurrentPage()).getMediaBox().getWidth()-(2*margin);
			    final float colWidth = tableWidth/(float)cols;
			    float[] colsWidth = new float[cols];
			    Arrays.fill(colsWidth, colWidth);
			    for (int i = 0; i < cols; i++) {
			        if(((TableCell)cont.getContent()[0][i]).getCellWidth() > 0){
			        	colsWidth[i] = (tableWidth / 100) * ((TableCell)cont.getContent()[0][i]).getCellWidth();
			        }
			    }
			    
				int startIdx = 0;
				int endIdx = 0;
				final int headerIdx = 0;
				float headerRowHeight = rowHeight;
				float tabHeight = 0;
				for(int i = 0; i < cont.getContent().length; i++){
					float rowH = rowHeight;
					for(int j = 0 ; j < cont.getContent()[i].length; j++){
				        String text;
			            if(cont.getContent()[i][j] != null){
			            	text = cont.getContent()[i][j].getText();
			            }else{
			            	continue;
			            }
				        float textwidth = (pdfDocument.getFont(cont.getFontType()).getStringWidth(text) / 1000.0f) * cont.getPoliceSize();
				        float cellWidth = colsWidth[j] - (2 * cellMargin);
				        int lineNb = 0;
				        if(textwidth > cellWidth){
				        	String []textWords = text.split(" ");
				        	String textLine = "";
				        	int textWidthTarget = (int)(cellWidth / charWidth);
			            	for(String word : textWords){
			            		if(!textLine.isEmpty() && (textLine.length() + word.length() + 1) <= textWidthTarget){
			            			textLine += " " + word;
			            		}else{
			            			textLine = word;
			            			lineNb++;
			            		}
			            	}
				        }
				        if(rowH < (rowHeight * lineNb)){
				        	rowH = rowHeight * lineNb;
				        }
		        	}
					if(i == headerIdx){
						headerRowHeight = rowH;
					}
					tabHeight += rowH;
					if((tabHeight <= availableHeight && (i == (cont.getContent().length - 1))) || tabHeight > availableHeight ){
						if(tabHeight <= availableHeight && i == (cont.getContent().length - 1)){
							endIdx = i;
						}
						TableCell[][] tmpCont;
						if(startIdx != 0){
							tmpCont = new TableCell[ endIdx - startIdx + 2][cols];
							tmpCont[0] = (TableCell[]) cont.getContent()[0];
							for(int tmpIdx = startIdx; tmpIdx <= endIdx; tmpIdx++){
								tmpCont[tmpIdx - startIdx + 1] = (TableCell[]) cont.getContent()[tmpIdx];
							}
						}else{
							tmpCont = (TableCell[][]) Arrays.copyOfRange(cont.getContent(), startIdx, endIdx + 1 );
						}
						PdfContent tmp1 = new PdfContent(cont);
						tmp1.setContent(tmpCont);
						drawTable(doc, doc.getPage(pdfDocument.getCurrentPage()), pdfDocument, tmp1);
						if(i < (cont.getContent().length - 1)){
							pdfDocument.newPage();
							PDPage page = new PDPage(PDRectangle.A4);
						    doc.addPage( page );
						    availableHeight = pdfDocument.getY() - pdfDocument.getMargin();
						    endIdx++;
						    startIdx = endIdx;
						    tabHeight = headerRowHeight;
						}
					} else{
						endIdx = i;
					}
				}
			} else{
				float availableHeight;
				int startIdx = 0;
				int endIdx = 0;
				boolean withFirstLine = true;
				for(int i = 0; i < cont.getContent().length; i++){
					availableHeight = pdfDocument.getY() - (pdfDocument.getMargin() * 2);
					if((((i + 1) * rowHeight) > availableHeight) || 
							(((i + 1) * rowHeight) <= availableHeight && (i == (cont.getContent().length - 1)))){
						
						if( i == (cont.getContent().length - 1)){
							endIdx = i;
						}
						if(endIdx != startIdx || (endIdx == (cont.getContent().length - 1))){
							if(signatureIdx == (contIdx + 1) && endIdx == (cont.getContent().length - 1)){
								float space4signature = (pdfDocument.getY() - (pdfDocument.getMargin() * 2) - ((endIdx - startIdx + 1) * rowHeight)) - ( pdfDocument.getPdfContent().get(signatureIdx).getContent().length * rowHeight);
								if(space4signature < 0){
									pdfDocument.newPage();
									PDPage page = new PDPage(PDRectangle.A4);
								    doc.addPage( page );
								}
							}
							ElementContent[][] tmpCont = Arrays.copyOfRange(cont.getContent(), startIdx, endIdx + 1);
							PdfContent tmp1 = new PdfContent(cont);
							tmp1.setContent(tmpCont);
							drawParagraph(doc, doc.getPage(pdfDocument.getCurrentPage()), pdfDocument, tmp1, withFirstLine);
							withFirstLine = false;
							endIdx++;
							startIdx = endIdx;
						}else if(i < (cont.getContent().length - 1)){
							// add new page 
							pdfDocument.newPage();
							PDPage page = new PDPage(PDRectangle.A4);
						    doc.addPage( page );
						}
					}else{
						endIdx = i;
					}
				}
			}
		}
	}

	public static RestResponse generate(PdfDocument pdfDocument, PDFTemplate pdfTemplate, Map<String, String> model, String pdfName) 
			throws IOException{
		String fontPath = "arial.ttf";
		String fontBoldPath = "arial_bold.ttf";
		String fontItalicPath = "arial_italic.ttf";
		ClassPathResource fontFile = new ClassPathResource(PDF_TEMPLATE_DIR + fontPath);
		ClassPathResource fontBoldFile = new ClassPathResource(PDF_TEMPLATE_DIR + fontBoldPath);
		ClassPathResource fontItalicFile = new ClassPathResource(PDF_TEMPLATE_DIR + fontItalicPath);
		ClassPathResource templateFile = new ClassPathResource(PDF_TEMPLATE_DIR + pdfTemplate.getTemplateName());
		PDDocument doc = PDDocument.load(templateFile.getInputStream(), MemoryUsageSetting.setupTempFileOnly());
		PDType0Font font = PDType0Font.load(doc, fontFile.getInputStream());
		PDType0Font fontBold = PDType0Font.load(doc, fontBoldFile.getInputStream());
		PDType0Font fontItalic = PDType0Font.load(doc, fontItalicFile.getInputStream());
		pdfDocument.setFontNormal(font);
		pdfDocument.setFontBold(fontBold);
		pdfDocument.setFontItalic(fontItalic);
		RestResponse response = fillFormData(doc, model);
		if(response.getCode() != 0) {
			return response;
		}
		write(doc, pdfDocument);
        log.info("generate: saving pdf into file : " + pdfName);
        File generatedPdfFile = new File(OUTBOX, pdfName);
        doc.save(generatedPdfFile);
        response.setData(generatedPdfFile);
        doc.close();
		return response;
	}

	public static RestResponse generate(PDFTemplate pdfTemplate, Map<String, String> model, String pdfName) 
			throws IOException{
		ClassPathResource templateFile = new ClassPathResource(PDF_TEMPLATE_DIR + pdfTemplate.getTemplateName());
		PDDocument doc = PDDocument.load(templateFile.getInputStream(), MemoryUsageSetting.setupTempFileOnly());
		RestResponse response = fillFormData(doc, model);
		if(response.getCode() != 0) {
			return response;
		}
        log.info("generate: saving pdf into file : " + pdfName);
        File generatedPdfFile = new File(OUTBOX, pdfName);
        doc.save(generatedPdfFile);
        response.setData(generatedPdfFile);
        doc.close();
		return response;
	}
	
	public static RestResponse generate(PdfDocument pdfDocument, PDFTemplate pdfTemplate, Map<String, String> model) 
			throws IOException{
		String dateTime = LocalDateTime.now().format(dtFormatter);
        String generatedPDFName = pdfTemplate.getTemplateName() + "." + dateTime + ".pdf";
		return generate(pdfDocument, pdfTemplate, model, generatedPDFName);
	}
}
