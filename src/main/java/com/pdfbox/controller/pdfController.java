package com.pdfbox.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdfbox.domain.dto.*;
import com.pdfbox.service.PDFGenerator;
import com.pdfbox.web.errors.RestResponse;

@RestController
public class pdfController {
	@RequestMapping("/")
	public String hello() {

		RestResponse resp = new RestResponse();
		Map<String, String> model = new HashMap<>();
		model.put("TextBox1", "Lorem Ipsum is \n simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
		resp = PDFGenerator.generate(PDFTemplate.TEST, model);
		System.out.println(resp.getCode());
		System.out.println(resp.getMessage());
		
		resp = null;
		
		// table example : 
		
		model.put("TextBox1", "");

		TableCell[][] tableContent = new TableCell[2][2];
		tableContent[0][0] = new TableCell("header 1", TableCell.ALIGNEMENT_CENTER, true);
		tableContent[0][1] = new TableCell("header 2", TableCell.ALIGNEMENT_CENTER, true);
		tableContent[1][0] = new TableCell("text 1", TableCell.ALIGNEMENT_LEFT);
		tableContent[1][1] = new TableCell("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s", TableCell.ALIGNEMENT_LEFT);

		ElementContent[][] cumulMsg = {{new ElementContent("text of the printing and typesetting industry. Lorem Ipsum has been the")}};
		ElementContent[][] SIGNATURE = {{new ElementContent("text of the printing and typesetting")}};
		List<PdfContent> pdfContent = new ArrayList<>(); 
		pdfContent.add(new PdfContent(cumulMsg));
		pdfContent.add(new PdfContent(tableContent, true));
		pdfContent.add(new PdfContent(cumulMsg));
		pdfContent.add(new PdfContent(SIGNATURE, PdfContent.ALIGNEMENT_SIGNATURE));
		PdfDocument pdfDocument = new PdfDocument(pdfContent);
		try {
			resp = PDFGenerator.generate(pdfDocument, PDFTemplate.TEST, model, "testTable.pdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println(resp.getCode());
		System.out.println(resp.getMessage());
		
		
		return "Hello pdfBox";
	}
}
