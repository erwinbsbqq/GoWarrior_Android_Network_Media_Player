package com.gowarrior.nmp.common;

import org.json.JSONException;
import org.json.JSONObject;  
import org.json.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import android.content.Context;
import android.util.Log;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/** 
 * Function: 
 * 		1. Use DOM to transform the XML file to DOM Document,String;
 * 		2. Change Xml String to JSON String.
 * 		3. Read/Write local files
 *  
 */  
public class TransUtility {	
    private static final String TAG="TranUtility";
  
    public TransUtility(){

    }

    /**
     * * Function: Use XML file to generate the object of the DOM Document.
     * *
     * * @param filePath: the XML file name
     * * @return : The object of the DOM Document
     * * @throws Exception
     * */
    public static Document xml2Doc(String filePath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        FileInputStream inputStream = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            // Get the XML file through the path.
            File file = new File(filePath);
            doc = builder.parse(file);

            // Get the XML file through the URL
            // URI uri = new URI(filePath);//filePath="http://java.sun.com/index.html"
            // doc = builder.parse(uri.toString());

            // Get the XML file through the Java IO stream
            // inputStream = new FileInputStream(filePath);

            return doc;
        } catch (Exception e) {
            Log.d(TAG, "xml2Doc(), exceptions");
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * * Function: Transform the Document object to String without indent
     * *
     * * @param doc : The Document object of the XML file;
     * * @return String
     * */
    public static String doc2String(Document doc){
        try {
            Source source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * * Function : Transform the string to the object of DOM Document
     * *
     * * @param xml : String
     * * @return : The object of the DOM Document
     * */
    public static Document string2Doc(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        InputSource source = null;
        StringReader reader = null;
        try {
            builder = factory.newDocumentBuilder();
            reader = new StringReader(xml);
            source = new InputSource(reader);
            doc = builder.parse(source);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(reader != null){
                reader.close();
            }
        }
    }

    /**
     * * Function : Transform the object of DOM Document to the XML file
     * *
     * * @param doc : The object of the DOM Document
     * * @param path : The path to save the XML file
     * */
    public static void doc2XML(Document doc, String path) {
        try {
            Source source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * * Function : Transform the XML string to the JSON string;
     * *
     * * @param xmlString : the XML String
     * * @param : Return the JSON String
     * */
    public String xmlStr2JsonStr(String xmlString){
        try {
            JSONObject jsonObj = XML.toJSONObject(xmlString);
            return jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * * Function: To Save the String as the local file with no style
     * *
     * * @param fileName : The local file to save the String
     * * @param writeStr : The saved String data
     * * @throws IOException
     * */
    public void writeFile(String fileName,String writeStr) throws IOException{
        try{
            File file = new File(fileName);
            if(false == file.exists()){
                Log.d(TAG, "Failed to open the file:"+fileName);
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte [] bytes = writeStr.getBytes();
            fos.write(bytes);
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * * Function : To read the local file content
     * *
     * * @param fileName : The local file to read
     * * @return : The file content in String style
     * * @throws IOException
     * */
    public String readFile(String fileName) throws IOException{
        String res="";
        try{
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte [] buffer = new byte[length];

            fis.read(buffer);
            res = new String(buffer);
            fis.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }

    /**
     * * Function : Transform the XML file to the JSON string;
     * *
     * * @param xmlPath : The path of the XML file
     * * @param : Return the JSON String
     * */
    public String xmlFile2JsonStr(String xmlPath){
        try {
            //Read the XML files with the DOM Document styles
            // Document doc = xml2Doc(xmlPath) ;
            // String xmlString = doc2String(doc);
            String xmlString = readFile(xmlPath);
            JSONObject jsonObj = XML.toJSONObject(xmlString);
            return jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


