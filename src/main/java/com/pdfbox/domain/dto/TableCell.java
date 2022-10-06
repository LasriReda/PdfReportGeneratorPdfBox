package com.pdfbox.domain.dto;


public class TableCell extends ElementContent{

	public static final int ALIGNEMENT_CENTER = 10;
	public static final int ALIGNEMENT_LEFT = 11;
	public static final int ALIGNEMENT_RIGHT = 12;
	private int align = ALIGNEMENT_LEFT;
	private boolean header = false;
	private int cellWidth = -1;
	
	public TableCell(String text){
		super(text);
	}
	
	public TableCell(TableCell tableCell){
		super(tableCell.getText());
		this.setAlign(tableCell.getAlign());
		this.setCellWidth(tableCell.getCellWidth());
		this.setHeader(tableCell.isHeader());
	}
	
	public TableCell(String text, int align){
		super(text);
		this.setAlign(align);
	}
	
	public TableCell(String text, int align, int cellWidth){
		super(text);
		this.setAlign(align);
		this.setCellWidth(cellWidth);
	}
	
	public TableCell(String text, int align, boolean isHeader){
		super(text);
		this.setAlign(align);
		this.setHeader(isHeader);
	}
	
	public TableCell(String text, int align, boolean isHeader, int cellWidth){
		super(text);
		this.setAlign(align);
		this.setHeader(isHeader);
		this.setCellWidth(cellWidth);
	}
	
	public TableCell(String text, boolean isHeader){
		super(text);
		this.setHeader(isHeader);
	}
	
	public TableCell(String text, boolean isHeader, int cellWidth){
		super(text);
		this.setHeader(isHeader);
		this.setCellWidth(cellWidth);
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int rightAlign) {
		this.align = rightAlign;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}
	
}
