package com.pdfbox.domain.dto;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

public class PdfDocument {
	public static final int FONT_NORMAL = 11;
	public static final int FONT_BOLD = 12;
	public static final int FONT_ITALIC = 13;
	private List<PdfContent> pdfContent;
	private float y = 570f;
	private float margin = 40f;
	private PDFont fontNormal;
	private PDFont fontBold;
	private PDFont fontItalic;
	private int currentPage = 0;
	private float tableRowHeight = 20f;
	private float tableCellMargin=5f;

	public PdfDocument(List<PdfContent> pdfContent) {
		super();
		this.pdfContent = pdfContent;
	}

	public PdfDocument(List<PdfContent> pdfContent, float y, float margin) {
		super();
		this.pdfContent = pdfContent;
		this.y = y;
		this.margin = margin;
	}
	public PdfDocument(List<PdfContent> pdfContent, float y, float margin, PDFont font) {
		super();
		this.pdfContent = pdfContent;
		this.y = y;
		this.margin = margin;
		this.fontNormal = font;
	}
	
	public PDFont getFont(int fontType){
		switch(fontType){
		case FONT_BOLD:
			return fontBold;
		case FONT_ITALIC:
			return fontItalic;
		case FONT_NORMAL:
		default:
			return fontNormal;
		}
	}
	
	public void newPage(){
		this.currentPage++;
		this.y = 780f;
	}
	
	public List<PdfContent> getPdfContent() {
		return pdfContent;
	}
	public void setPdfContent(List<PdfContent> pdfContent) {
		this.pdfContent = pdfContent;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getMargin() {
		return margin;
	}
	public void setMargin(float margin) {
		this.margin = margin;
	}
	public PDFont getFontNormal() {
		return fontNormal;
	}
	public void setFontNormal(PDFont font) {
		this.fontNormal = font;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public PDFont getFontBold() {
		return fontBold;
	}

	public void setFontBold(PDFont fontBold) {
		this.fontBold = fontBold;
	}

	public PDFont getFontItalic() {
		return fontItalic;
	}

	public void setFontItalic(PDFont fontItalic) {
		this.fontItalic = fontItalic;
	}

	public float getTableRowHeight() {
		return tableRowHeight;
	}

	public void setTableRowHeight(float tableRowHeight) {
		this.tableRowHeight = tableRowHeight;
	}

	public float getTableCellMargin() {
		return tableCellMargin;
	}

	public void setTableCellMargin(float tableCellMargin) {
		this.tableCellMargin = tableCellMargin;
	}
	
	
}
