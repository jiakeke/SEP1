package controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeControllerTest {

    /**
     * 测试：getCellValueIgnoreCase
     */
    @Test
    void testGetCellValueIgnoreCase() {
        Map<String, Object> row = new HashMap<>();
        row.put("Name", "Alice");
        row.put("math", 85.5);  // 小写

        assertEquals("Alice", GradeController.getCellValueIgnoreCase(row, "Name"));
        assertEquals(85.5, GradeController.getCellValueIgnoreCase(row, "MATH"));
        assertNull(GradeController.getCellValueIgnoreCase(row, "Science"));
    }

    /**
     * 测试：formatCellValue
     */
    @Test
    void testFormatCellValue() {
        assertEquals("Alice", GradeController.formatCellValue("Alice"));
        assertEquals("85.50", GradeController.formatCellValue(85.5));
        assertEquals("100.00", GradeController.formatCellValue(100));
        assertEquals("", GradeController.formatCellValue(null));
    }

    /**
     * 测试：createPageEvent
     */
    @Test
    void testCreatePageEvent() {
        PdfPageEventHelper pageEvent = GradeController.createPageEvent();
        assertNotNull(pageEvent);
    }

    /**
     * 测试：addDocumentHeader
     * 这里不需要真的创建PDF，只需mock Document对象，确认add()调用就行
     */
    @Test
    void testAddDocumentHeader() throws DocumentException {
        Document mockDocument = mock(Document.class);

        GradeController.addDocumentHeader(mockDocument, "Test Group");

        // 至少两次add调用（标题+导出时间），有可能还有换行
        verify(mockDocument, atLeast(2)).add(any(Paragraph.class));
    }
}
