package com.e3ps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.aspose.words.Document;
import com.aspose.words.PageSetup;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.e3ps.common.aspose.AsposeUtils;

public class AsposeWordTest {

	public static void main(String[] args) throws Exception {
		// Aspose.Words 라이선스 설정 (필요한 경우)
		// License license = new License();
		// license.setLicense("Aspose.Words.lic");

		AsposeUtils.setAsposeWordLic();
		// Word 문서 경로
		String documentPath = "D:\\path_to_output_document.docx";

		// 전자서명 이미지 생성
		BufferedImage signatureImage = createSignatureImage();

		// Aspose.Words 문서 열기
		Document doc = new Document(documentPath);

		// 이미지를 삽입할 페이지 및 좌표 설정
		int pageNumber = 1; // 페이지 번호 (1부터 시작)
		float left = 100; // 왼쪽 좌표 (포인트 단위)
		float top = 100; // 상단 좌표 (포인트 단위)

		// 전자서명 이미지 삽입
		insertImageAtLocation(doc, signatureImage, pageNumber, left, top);

		// 문서를 저장
		doc.save("D:\\path_to_output_document33.docx");
	}

	public static void insertImageAtLocation(Document doc, BufferedImage image, int pageNumber, float left, float top)
			throws Exception {
		// 이미지 삽입 옵션 설정
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "png", outputStream);
		byte[] imageBytes = outputStream.toByteArray();

		// 페이지 인덱스를 0부터 시작하므로 실제 페이지 번호를 인덱스로 변환합니다.
		int pageIndex = pageNumber - 1;

		// 이미지를 삽입할 위치 계산
		float x = left * 72.0f / 96.0f; // 포인트를 픽셀로 변환
		float y = top * 72.0f / 96.0f; // 포인트를 픽셀로 변환

		// 이미지 노드 삽입
		
		BufferedImage
		Shape imageShape = new Shape(doc, ShapeType.IMAGE);
		imageShape.getImageData().setImage(imageBytes);
		imageShape.setWidth(x);
		imageShape.setHeight(y);

		// 페이지에 이미지 추가
		doc.getPages().get(pageIndex).getBody().appendChild(imageShape);
	}

	public static BufferedImage createSignatureImage() {
		// 여기에서 전자서명 이미지를 생성하거나 가져오는 로직을 구현합니다.
		// 이 예제에서는 더미 이미지를 생성하여 반환합니다.

		int width = 200;
		int height = 100;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		// 전자서명 그리기 (예: 검은색 동그라미)
		g2d.setColor(Color.BLACK);
		g2d.fillOval(50, 25, 100, 50);

		g2d.dispose();
		return image;
	}
}