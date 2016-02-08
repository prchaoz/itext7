package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagutils.PdfTagStructure;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class FormFieldsTaggingTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldsTaggingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldsTaggingTest/";

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    /**
     * Form fields addition to the tagged document.
     */
    @Test
    public void formFieldTaggingTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms01.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setTagged();

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields copying from the tagged document.
     */
    @Test
    public void formFieldTaggingTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms02.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, true);
        acroForm.addField(PdfFormField.createCheckBox(pdfDoc, new Rectangle(36, 560, 20, 20), "TestCheck", "1"));

        PdfDocument docToCopyFrom = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms07.pdf"));
        docToCopyFrom.copyPages(1, docToCopyFrom.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields flattening in the tagged document.
     */
    @Test
    public void formFieldTaggingTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms03.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms01.pdf"), new PdfWriter(outFileName));

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, false);
        acroForm.flattenFields();

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Removing fields from tagged document.
     */
    @Test
    public void formFieldTaggingTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms04.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms01.pdf"), new PdfWriter(outFileName));

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, false);
        acroForm.removeField("TestCheck");
        acroForm.removeField("push");

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields flattening in the tagged document (writer mode).
     * TODO bug with forms flattening: radiobuttons are not flattened
     */
    @Test
    public void formFieldTaggingTest05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms05.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms05.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        form.flattenFields();

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Removing fields from tagged document (writer mode).
     */
    @Test
    public void formFieldTaggingTest06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms06.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms06.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        form.removeField("TestCheck");
        form.removeField("push");

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Addition of the form field at the specific position in tag structure.
     */
    @Test
    public void formFieldTaggingTest07() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms07.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms07.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocWithFields.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        // Original document is already tagged, so there is no need to mark it as tagged again
//        pdfDoc.setTagged();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfButtonFormField pushButton = PdfFormField.createPushButton(pdfDoc, new Rectangle(36, 650, 40, 20), "push", "Capcha");

        PdfTagStructure tagStructure = pdfDoc.getTagStructure();
        tagStructure.moveToKid(PdfName.Div);
        acroForm.addField(pushButton);

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    private void addFormFieldsToDocument(PdfDocument pdfDoc, PdfAcroForm acroForm) {
        Rectangle rect = new Rectangle(36, 700, 20, 20);
        Rectangle rect1 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdfDoc, "TestGroup", "1");

        PdfFormField.createRadioButton(pdfDoc, rect, group, "1");
        PdfFormField.createRadioButton(pdfDoc, rect1, group, "2");

        acroForm.addField(group);

        PdfButtonFormField pushButton = PdfFormField.createPushButton(pdfDoc, new Rectangle(36, 650, 40, 20), "push", "Capcha");
        PdfButtonFormField checkBox = PdfFormField.createCheckBox(pdfDoc, new Rectangle(36, 560, 20, 20), "TestCheck", "1");

        acroForm.addField(pushButton);
        acroForm.addField(checkBox);
    }

    private void compareOutput(String outFileName, String cmpFileName) throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String compareResult = compareTool.compareTagStructures(outFileName, cmpFileName);
        if (compareResult != null) {
            Assert.fail(compareResult);
        }

        compareResult = compareTool.compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + outFileName);

        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }
}