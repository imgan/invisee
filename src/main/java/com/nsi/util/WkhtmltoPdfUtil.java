/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.util;

//import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
//import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author hatta.palino
 */
public class WkhtmltoPdfUtil {

    private final Logger logger = Logger.getLogger(this.getClass());
    
    private String getVelocity(String filePatern, Object data) throws IOException, ParseException {
        VelocityContext vc = new VelocityContext();
        vc.put("data", data);
        vc.put("date", new Date());
        vc.put("numberTool", new NumberTool());
        vc.put("dateTool", new DateTool());
        
        Resource resource = new ClassPathResource("email/" + filePatern);
        File patern = resource.getFile();
        String string = FileUtils.readFileToString(patern, Charset.defaultCharset());
        RuntimeServices rs = RuntimeSingleton.getRuntimeServices();
        StringReader sr = new StringReader(string);
        SimpleNode sn = rs.parse(sr, "1");

        Template t1 = new Template();
        t1.setRuntimeServices(rs);
        t1.setData(sn);
        t1.initDocument();

        StringWriter sw1 = new StringWriter();
        t1.merge(vc, sw1);
        
        return sw1.toString();
    }
    
    public byte[] getPdf(String filePatern, Object data, String encrypt) {
        try {
            String name = UUID.randomUUID().toString();
            
            String content = getVelocity(filePatern, data);
            logger.info("content : " + content);

            File file = new File("");

//            Pdf pdf = new Pdf();
//            pdf.addPageFromString(content);
//            pdf.addParam(new Param("--no-footer-line"));
//            pdf.addParam(new Param("--enable-javascript"));
//            File file = pdf.saveAs(name);
            byte[] bytes;
            
            if(encrypt != null) {
                System.out.println("encrypt : " + encrypt);
                File tempFile = new File("en_" + name + ".pdf");
                
                PdfReader reader = new PdfReader(new FileInputStream(file));
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tempFile));
                stamper.setEncryption(encrypt.getBytes(), encrypt.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
                stamper.close();
                reader.close();

                bytes = IOUtils.toByteArray(new FileInputStream(tempFile));
                tempFile.delete();
            } else {
                bytes = IOUtils.toByteArray(new FileInputStream(file));
            }
            file.delete();
            return bytes;
            
        
        } catch (DocumentException | IOException | ParseException ex) {
            logger.error(ex);
            return null;
        } 
    }
}
