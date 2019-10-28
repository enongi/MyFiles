package com.inspur.crds.platform.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 其实docx属于zip的一种，这里只需要操作word/document.xml中的数据，其他的数据不用动
 */
public class XmlToDocx {
    private static Configuration cfg = null;

    /**
     * @param documentFile 动态生成数据的docunment.xml文件
     * @throws ZipException
     * @throws IOException
     */
    public synchronized static void outDocx(File documentFile, File headerFile, File docxFile, String toFilePath
                        ,String imgPath) throws ZipException, IOException {

        try {
            //doc模板路径
            //File docxFile= new File(docxTemplate);
            ZipFile zipFile = new ZipFile(docxFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(toFilePath));
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                //把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                zipout.putNextEntry(new ZipEntry(next.toString()));

                if ("word/document.xml".equals(next.toString())) {
                    if (documentFile != null) {
                        InputStream in = new FileInputStream(documentFile);
                        while ((len = in.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        in.close();
                    }
                } else if ("word/header1.xml".equals(next.toString())) {
                    if (documentFile != null) {
                        InputStream in = new FileInputStream(headerFile);
                        while ((len = in.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        in.close();
                    }
                }//图片替换
                else if(next.toString().endsWith("png")){
                    String imgName_full = next.toString();
                    String imgName = imgName_full.substring(imgName_full.lastIndexOf("/")+1);
                    File img = new File (imgPath+imgName);
                    if(img!=null) {
                        InputStream in = new FileInputStream(img);
                        while((len = in.read(buffer))!=-1){
                            zipout.write(buffer,0,len);
                        }
                        in.close();
                    }else {
                        while((len = is.read(buffer))!=-1){
                            zipout.write(buffer,0,len);
                        }
                        is.close();
                    }
                }
                else {
                    while((len = is.read(buffer))!=-1){
                        zipout.write(buffer,0,len);
                    }
                    is.close();
                }
            }
            zipout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param documentFile 动态生成数据的docunment.xml文件 替换图片
     * @throws ZipException
     * @throws IOException
     */
    public synchronized static void outDocxWithImg(File documentFile, File headerFile, File docxFile, String toFilePath, Map<String, File> imgs) throws ZipException, IOException {

        try {
            //doc模板路径
            //File docxFile= new File(docxTemplate);
            ZipFile zipFile = new ZipFile(docxFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(toFilePath));
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                //把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                zipout.putNextEntry(new ZipEntry(next.toString()));

                if ("word/document.xml".equals(next.toString())) {
                    if (documentFile != null) {
                        InputStream in = new FileInputStream(documentFile);
                        while ((len = in.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        in.close();
                    }
                } else if ("word/header1.xml".equals(next.toString())) {
                    if (documentFile != null) {
                        InputStream in = new FileInputStream(headerFile);
                        while ((len = in.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        in.close();
                    }
                }//图片替换
                //else if("word/media/image2.png".equals(next.toString().endsWith("png"))){
                else if (next.toString().endsWith("png")) {
                    String imgName_full = next.toString();
                    String imgName = imgName_full.substring(imgName_full.lastIndexOf("/") + 1, imgName_full.lastIndexOf(".png"));
                    File img = imgs.get(imgName);
                    if (img != null) {
                        InputStream in = new FileInputStream(img);
                        while ((len = in.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        in.close();
                    } else {
                        while ((len = is.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        is.close();
                    }
                } else {
                    while ((len = is.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    is.close();
                }
            }
            zipout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param templatefile 模板文件
     * @param param        需要填充的内容
     * @param out          填充完成输出的文件
     * @throws IOException
     * @throws TemplateException
     */
    public synchronized static void process(String templatefile, String mod_path, Map param, Writer out) throws IOException, TemplateException {
        if (cfg == null) {
            cfg = new Configuration();
            cfg.setDefaultEncoding("UTF-8");
            cfg.setDirectoryForTemplateLoading(new File(mod_path));
        }
        //获取模板
        Template template = cfg.getTemplate(templatefile, "UTF-8");
        template.setOutputEncoding("UTF-8");
        //合并数据
        template.process(param, out);
        if (out != null) {
            out.close();
        }
    }
//	public static void main(String[] args) throws IOException, TemplateException {
//		String type = "ctz";
//		//xml的模板路径*/*
//		String xmlTemplate = "D:\\template\\rating\\template\\document.xml";
//
//		//设置docx的模板路径 和文件名
//		String docxTemplate = "D:\\template\\rating\\template\\bank.docx";
//		String uuid = UUID.randomUUID().toString().replace("-","");
//		String toFilePath = "D:\\template\\rating\\temp\\"+type+uuid+".docx";
//
//		//填充完数据的临时xml
//		String xmlTemp = "D:\\template\\rating\\temp\\temp.xml";
//		Writer w = new FileWriter(new File(xmlTemp));
//		String xmlTemph = "D:\\template\\rating\\temp\\temph.xml";
//		Writer wh = new FileWriter(new File(xmlTemph));
//
//		//1.需要动态传入的数据
//		Map<String,Object> p = new HashMap<String,Object>();
//
//		Map<String,String> map=new HashMap<String,String>();
//		map.put("DEBT_CODE", "11");
//		map.put("DEBT_NAME", "22");
//		map.put("RATING_LEVEL", "33");
//		map.put("HIDE_LEVEL", "44");
//		map.put("DEBT_MATURE", "55");
//		map.put("BEGIN_TIME", "66");
//		map.put("DEBT_TERM", "77");
//		map.put("CREDIT_AMOUNT", "88");
//		map.put("ENCASH_WAY", "99");
//		map.put("DEBT_RATE", "00");
//		map.put("ZXCS", "13");
//		Map<String,String> map2=new HashMap<String,String>();
//		map2.put("DEBT_CODE", "111");
//		map2.put("DEBT_NAME", "222");
//		map2.put("RATING_LEVEL", "333");
//		map2.put("HIDE_LEVEL", "444");
//		map2.put("DEBT_MATURE", "555");
//		map2.put("BEGIN_TIME", "666");
//		map2.put("DEBT_TERM", "777");
//		map2.put("CREDIT_AMOUNT", "888");
//		map2.put("ENCASH_WAY", "999");
//		map2.put("DEBT_RATE", "000");
//		map2.put("ZXCS", "1313");
//		List<Map<String,String>> zqjbqk_list = new ArrayList<Map<String,String>>();
//		zqjbqk_list.add(map);
//		zqjbqk_list.add(map2);
//		zqjbqk_list.add(map2);
//		zqjbqk_list.add(map2);
//
//		p.put("sw_industry", "sw_industry");
//		p.put("inner_industry", "inner_industry");
//		p.put("rating_level", "rating_level");
//		p.put("is_market", "is_market");
//		p.put("is_abroad", "is_abroad");
//		p.put("kggd", "kggd");
//		p.put("sjkzr", "sjkzr");
//		p.put("qugk", "qugk");
//		p.put("zyyw", "zyyw");
//		p.put("azzlwlx", "azzlwlx");
//		p.put("srylrjg", "srylrjg");
//		p.put("zczl", "zczl");
//		p.put("jgzb", "jgzb");
//		p.put("zwjg", "zwjg");
//		p.put("cznl", "cznl");
//		p.put("ys", "ys");
//		p.put("gzfxd", "gzfxd");
//		p.put("jl", "jl");
//		p.put("company_name", "万科");
//		p.put("year", "1999");
//		p.put("rating_date", "1999年11月11日");
//		p.put("zqjbqk_list", zqjbqk_list);
//		p.put("type", type);
//
//		List zycw_list = new ArrayList();
//		Map map11 = new HashMap();
//		map11.put("FIN_DATE","2015/12/31");
//		map11.put("1","11");
//		map11.put("2","22");
//		map11.put("3","33");
//		Map map12 = new HashMap();
//		map12.put("FIN_DATE","2016/12/31");
//		map12.put("1","111");
//		map12.put("2","222");
//		map12.put("3","333");
//		Map map13 = new HashMap();
//		map13.put("FIN_DATE","2017/12/31");
//		map13.put("1","1111");
//		map13.put("2","2222");
//		map13.put("3","3333");
//		zycw_list.add(map11);
//		zycw_list.add(map12);
//		zycw_list.add(map13);
//		List zycw_h_list = new ArrayList();
//		Map map21 = new HashMap();
//		map21.put("TITLE","title1");
//		Map map22 = new HashMap();
//		map22.put("TITLE","title2");
//		Map map23 = new HashMap();
//		map23.put("TITLE","title3");
//		zycw_h_list.add(map21);
//		zycw_h_list.add(map22);
//		zycw_h_list.add(map23);
//
//
//		p.put("zycw_list", zycw_list);
//		p.put("zycw_h_list", zycw_h_list);
//		p.put("jgkj_list", zycw_list);
//		p.put("jgkj_h_list", zycw_h_list);
//		p.put("fxrjgkj_list", zycw_list);
//		p.put("fxrjgkj_h_list", zycw_h_list);
//
//
//		Map map31 = new HashMap();
//		map31.put("FIN_DATE","1111/12/12");
//		Map map32 = new HashMap();
//		map32.put("FIN_DATE","1112/12/12");
//		Map map33 = new HashMap();
//		map33.put("FIN_DATE","1113/12/12");
//		List qyjj_y_list = new ArrayList();
//		qyjj_y_list.add(map31);
//		qyjj_y_list.add(map32);
//		qyjj_y_list.add(map33);
//
//		Map map41 = new HashMap();
//		map41.put("1","1111");
//		Map map42 = new HashMap();
//		map42.put("1","1111");
//		Map map43 = new HashMap();
//		map43.put("1","1111");
//		Map map44 = new HashMap();
//		map44.put("1","1111");
//		Map map45 = new HashMap();
//		map45.put("1","1111");
//		Map map46 = new HashMap();
//		map46.put("1","1111");
//		List qyjj_list = new ArrayList();
//		qyjj_list.add(map41);
//		qyjj_list.add(map42);
//		qyjj_list.add(map43);
//		qyjj_list.add(map44);
//		qyjj_list.add(map45);
//		qyjj_list.add(map46);
//
//
//		List qyjj_h_list = zycw_h_list;
//		p.put("qyjj_y_list", qyjj_y_list);
//		p.put("qyjj_list", qyjj_list);
//		p.put("qyjj_h_list", qyjj_h_list);
//
//
//
//
//
//
//
//		//2.把map中的数据动态由freemarker传给xml
//		File file = new File(xmlTemplate);
//		long s = file.length();
//		try{
//			process("document.xml","D:\\template\\rating\\template", p, w);
//			process("header.xml","D:\\template\\rating\\template", p, wh);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//3.把填充完成的xml写入到docx中
//		File docxFile= new File(docxTemplate);
//		outDocx(new File(xmlTemp),new File(xmlTemph), docxFile, toFilePath);
//	}
}
