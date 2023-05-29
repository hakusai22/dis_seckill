package com.seckill.dis.gateway.utils;

//
//
//import java.io.*;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.function.Predicate;
//import java.util.jar.JarEntry;
//import java.util.jar.JarFile;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
///**
// * @Description: 类源码分解工具类
// */
//
public class ResolveClassCodeTools {

}
//
//    private static final List<String> COMMON_METHODS = Arrays.asList("toString", "wait", "equals", "hashCode", "getClass", "notify", "notifyAll");
//    private static final String SUB_PATH = "BOOT-INF/classes/";
//    public static  String PARENT_JAR_PATH;
//
//    //private static final String TEMP_CLASS_PATH = ResolveClassCodeTools.class.getClassLoader().getResource("").getPath() + "../../../agent/class";
//
//    //private static final String TEMP_RESOURCE_PATH = ResolveClassCodeTools.class.getClassLoader().getResource("").getPath() + "../../../agent/resource";
//    /**
//     * 根据class字节码和源码获取注解
//     * @param clazz class字节码
//     * @return
//     */
//    public static Map<Annotation,String> getClassAnnotationsByClassCode(Class<?> clazz,String classCode){
//        String[] codeLines = classCode.split("\n");
//        Map<String,Annotation> convertMap = new HashMap<>();
//        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
//            String annotationTypeName = annotation.annotationType().toString();
//            convertMap.put(annotationTypeName.substring(annotationTypeName.lastIndexOf(".") + 1),annotation);
//        }
//        return getAnnotationMapCore(convertMap,codeLines,(line)->line.startsWith("public class"));
//    }
//
//    private static Map<Annotation,String> getAnnotationMapCore(Map<String,Annotation> convertMap, String[] codeLines, Predicate<String> endJudge){
//        Map<Annotation,String> annotationMap = new HashMap<>();
//        for(String codeLine : codeLines){
//            String codeLineTrim = codeLine.trim();
//            if(endJudge.test(codeLineTrim)){
//                break;
//            }
//            if(codeLineTrim.length() > 0 && codeLineTrim.charAt(0) == '@'){
//                String annotationShortName;
//                if(codeLineTrim.contains("(")){
//                    annotationShortName = codeLineTrim.substring(1,codeLineTrim.indexOf("("));
//                }else{
//                    annotationShortName = codeLineTrim.substring(1);
//                }
//                if(convertMap.containsKey(annotationShortName)){
//                    annotationMap.put(convertMap.get(annotationShortName),codeLineTrim);
//                }
//            }
//        }
//        return annotationMap;
//    }
//
//    /**
//     * 根据class字节码和源码获取方法
//     * @param clazz class字节码
//     * @return
//     */
////    public static Map<Method,String> getClassMethodsByClassCode(Class<?> clazz,String classPath){
////        Set<Method> methods = getAllMethodByClass(clazz);
////        Map<Method,String> result = new HashMap<>();
////        //剔除掉lambda表达式产生的内部类方法
////        methods.stream().filter(e->e.getName().indexOf('$') == -1).forEach(e->{
////            String methodCode = Decompiler.decompile(classPath, e.getName(), true);
////            result.put(e,methodCode);
////        });
////        return result;
////    }
//
//    public static Set<Method> getAllMethodByClass(Class<?> clazz){
//        Set<Method> result = new HashSet<>(Arrays.asList(clazz.getMethods()));
//        result.addAll(Arrays.asList(clazz.getDeclaredMethods()));
//        return result.stream().filter(e->!COMMON_METHODS.contains(e.getName())).collect(Collectors.toSet());
//    }
//
//    /**
//     * 根据Method和源码获取注解
//     * @param method method
//     * @return
//     */
//    public static Map<Annotation,String> getMethodAnnotationsByMethodCode(Method method,String methodCode){
//        String[] codeLines = methodCode.split("\n");
//        Map<String,Annotation> convertMap = new HashMap<>();
//        for (Annotation annotation : method.getDeclaredAnnotations()) {
//            String annotationTypeName = annotation.annotationType().toString();
//            convertMap.put(annotationTypeName.substring(annotationTypeName.lastIndexOf(".") + 1),annotation);
//        }
//        String regex = "^(public)?(protect)?(private)?(default)?(static)?(.)*"+"heiheihei"+"\\((.*)\\)(\\{?)$";
//        return getAnnotationMapCore(convertMap,codeLines,(line)-> Pattern.matches(regex,line));
//    }
//
//    /**
//     * 根据方法详情获取方法调用详情源码信息
//     * @return
//     */
////    public static List<String> getCallNonStaticMethodBehaviorCodeByMethodDetail(MethodCodeDetail methodCodeDetail){
////        String methodCode = methodCodeDetail.code();
////        List<String> methodCodeLines = Arrays.asList(methodCode.split("\n"));
////        Set<String> result = new HashSet<>();
////        methodCodeLines.stream().filter(e-> e.trim().contains("this.")).forEach(e->{
////            int optCur = 0;
////            while (optCur < e.length()){
////                optCur = e.indexOf("this.",optCur);
////                if(optCur == -1){
////                    break;
////                }
////                int indexEnd = indexOfCallBehaviorEnd(e,optCur);
////                String thisCallStr = e.substring(optCur,indexOfCallBehaviorEnd(e,optCur));
////                if(indexEnd < e.length() && e.charAt(indexEnd) == '('){
////                    result.add(thisCallStr);
////                }
////                optCur += e.length();
////            }
////        });
////        return new ArrayList<>(result);
////    }
//
//    /**
//     * 根据注解信息返回注解的参数信息（通过String map的方式返回）
//     * @param annotationCode 注解的源码信息
//     * @param annotation 注解类型对象
//     * @return 注解参数map
//     */
//    public static Map<String,String> getAnnotationParamsInString(String annotationCode,Annotation annotation){
//        Map<String,String> params = new HashMap<>();
//        if(annotationCode.contains("(")){
//            String paramStr = annotationCode.substring(annotationCode.indexOf('(') + 1,annotationCode.length() - 1);
//            return groupParseConsiderBracket(paramStr,'=',',');
//        }
//        return params;
//    }
//
//    /**
//     * 将字符串按照分组解析成map
//     * @param source
//     * @param groupBy
//     * @param split
//     * @return
//     */
//    private static Map<String,String> groupParseConsiderBracket(String source, char groupBy, char split){
//        Map<String,String> paramMap = new HashMap<>();
//        StringBuilder key = new StringBuilder();
//        StringBuilder value = new StringBuilder();
//        StringBuilder cur = key;
//        Stack<Character> bracketStack = new Stack<>();
//        for(int i = 0 ; i < source.length() ; i ++){
//            char item = source.charAt(i);
//            Pair<Character,Integer> pair = StringUtils.coupleBrackets(item);
//            if(bracketStack.empty()){
//                if(pair.getSecond() <= 0){
//                    if(item == groupBy){
//                        cur = value;
//                    }else if(item == split){
//                        paramMap.put(key.toString().trim(), value.toString());
//                        key = new StringBuilder();
//                        value = new StringBuilder();
//                        cur = key;
//                    }else{
//                        cur.append(item);
//                    }
//                }else{
//                    bracketStack.push(item);
//                    cur.append(item);
//                }
//            }else{
//                if(pair.getSecond() < 0){
//                    Character peek = bracketStack.peek();
//                    if(peek.charValue() == pair.getFirst()){
//                        bracketStack.pop();
//                    }
//                    cur.append(item);
//                }else if(pair.getSecond() == 0){
//                    cur.append(item);
//                }else{
//                    Character peek = bracketStack.peek();
//                    if(peek.charValue() == pair.getFirst()){
//                        bracketStack.pop();
//                    }else{
//                        bracketStack.push(item);
//                    }
//                    cur.append(item);
//                }
//            }
//        }
//        paramMap.put(key.toString().trim(), value.toString());
//        return paramMap;
//    }
//
//    /**
//     * 获取括号中的所有字符串
//     * @param source
//     * @return
//     */
//    public static List<String> getAllStrFromBracket(String source){
//        source = source.substring(1,source.length() - 1).replace("\"","");
//        return Arrays.asList(source.split(","));
//    }
//
//    private static int indexOfCallBehaviorEnd(String source,int begin){
//        String org = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.";
//        for(int index = begin;index < source.length() ; index ++){
//            char i = source.charAt(index);
//            if(!org.contains(i+"")){
//                return index;
//            }
//        }
//        return source.length();
//    }
//
//    /**
//     * url拼接
//     * @param suffix 前缀
//     * @param prefix 后缀
//     * @return
//     */
//    public static String appendUrl(String prefix,String suffix){
//        if(prefix == null || "".equals(prefix)){
//            return suffix;
//        }
//
//        if(prefix.charAt(prefix.length() - 1) != '/'){
//            prefix += '/';
//        }
//        if(suffix == null || "".equals(suffix)){
//            return prefix;
//        }
//        if(suffix.charAt(0) == '/'){
//            suffix = suffix.substring(1);
//        }
//        return prefix + suffix;
//    }
//
//    /**
//     * 创建class文件，并把文件路径返回
//     * @param clazz 类字节码
//     * @return
//     */
//    public static String createClassFileTemp(Class<?> clazz,String jarPath,String tempPath) throws IOException {
//        //jarPath : /home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar
//        //dirUrl    file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT/class
//        //C:\Users\xuwenxing\IdeaProjects\hikAgent_linux\out\artifacts\hagent_jar\hikAgent.jar
//        //String dirUrl = TEMP_CLASS_PATH + jarName + "/" +  clazz.getName().substring(0,clazz.getName().lastIndexOf('.')) + "/";
//        String fileName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1) + ".class";
//        File classFile = readFileFromJarToLocal(jarPath,SUB_PATH+clazz.getName().replace(".", "/") + ".class",fileName,tempPath);
//        if(Objects.isNull(classFile)){
//            return null;
//        }
//        //dir : file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar!/BOOT-INF/classes!/com/hikvision/hikkan/ybase/aspect/QlyProjectNameFilter.class
//        System.out.println("HikAgent doing : "+classFile.getPath());
//        return tempPath + fileName;
//    }
//    /**
//     * @description 从jar包中读取jar文件创建在本地，并返回本地jar路径
//     * @Param jarPath
//     * @Return String
//     */
//    public static String createJarFileTemp(String jarPath) throws IOException{
//        //处理这个jarPath  如：file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar! /BOOT-INF/lib/kcommon-aop-1.4.0-SNAPSHOT.jar!/
//        //  parentJarPath :  file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar
//        String parentJarPath = getParentJarPath(jarPath);
//        JarFile jarFile = new JarFile(parentJarPath);
//        //将 jar包放在 /home/hik/data/ybase/jar/ 目录下
//        String toPath = parentJarPath.substring(0,parentJarPath.lastIndexOf("/")+1) + "jar/";
//        //获取 entry路径
//        String EntryPath = jarPath.substring(parentJarPath.length()+7,jarPath.length()-2);
//        JarEntry jarEntry = jarFile.getJarEntry(EntryPath);
//        InputStream inputStream = jarFile.getInputStream(jarEntry);
//        String jarName = jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/")+1).replace(".jar","");
//        //将 嵌套的二级jar包 写入到本地目录
//        File localFile = readFileFromInputStreamToLocal(inputStream, toPath, jarName);
//        return localFile.getPath();
//        }
//
//
//
//    /**
//     * @description 获取父级jar包路径
//     * @Param jarPath
//     * @Return
//     */
//    public static String getParentJarPath(String jarPath){
//        String[] strings = jarPath.split("/");
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < strings.length; i++) {
//            if (strings[i].contains(".jar!")){
//                String replace = strings[i].replace("jar!", "jar");
//                sb.append(replace);
//                break;
//            }
//            sb.append(strings[i] + "/");
//        }
//        PARENT_JAR_PATH = sb.substring(5);
//        return sb.substring(5);
//    }
//
//
//
//    /**
//     * 从jar包中读取文件
//     * @param jarPath jar包路径
//     * @param jarInnerFilePath 需要读取jar包的内部文件路径
//     * @param fileName 文件名称
//     * @param localCachePath 内部文件暂存路径
//     * @return 本地文件对象
//     * @throws IOException jar包中没有找到指定的jarInnerFilePath
//     */
//    public static File readFileFromJarToLocal(String jarPath,String jarInnerFilePath,String fileName,String localCachePath) throws IOException {
//        JarFile jarFile = new JarFile(jarPath);
//        JarEntry jarEntry = jarFile.getJarEntry(jarInnerFilePath);
//        InputStream in = jarFile.getInputStream(jarEntry);
//        return readFileFromInputStreamToLocal(in,localCachePath,fileName);
//    }
//
//    /**
//     * 从流中读取文件到本地
//     * @param in 输入流
//     * @param toPath 本地文件
//     * @param fileName 文件名称
//     * @return 本地文件对象
//     */
//    private static File readFileFromInputStreamToLocal(InputStream in,String toPath,String fileName){
//        OutputStream out = null;
//        try{
//            File dir = new File(toPath);
//            if(!dir.exists() && !dir.mkdirs()){
//                return null;
//            }
//            String fileUrl = toPath + fileName;
//            out = new FileOutputStream(fileUrl);
//            byte[] buff = new byte[1024];
//            int len;
//            while(Objects.nonNull(in) && (len=in.read(buff))!=-1){
//                out.write(buff, 0, len);
//            }
//            return new File(fileUrl);
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            try{
//                if(Objects.nonNull(in)){
//                    in.close();
//                }
//                if(Objects.nonNull(out)){
//                    out.close();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * @description  将含有 jar包路径的 classPath 转化为本地所存的path
//     * @Param classPath      file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar!/BOOT-INF/lib/kcommon-aop-1.4.0-SNAPSHOT.jar!/     linux双层jar
//     *                       file:/home/hik/data/ybase/ybase-core-1.5.0-SNAPSHOT.jar!/BOOT-INF/classes!/                                linux单层jar
//     * @Return String        file:/home/hik/data/ybase/agent/jar/jar名/全限定名
//     *                       file:/home/hik/data/ybase/class/全限定名
//                     C:\Users\xuwenxing\IdeaProjects\hikAgent_linux\out\artifacts\hagent_jar\hikAgent.jar!\   windows环境
//     */
//    public static String transPath(String classPath){
//        //对classPath进行扫描
//        String newPath = "";
//        String[] strings = classPath.split("/");
//        boolean isFirst = true;
//        boolean reAppear = false;
//        int symbolIndex = 0;
//        String jarName = null;
//
//        for (int i = 0; i < strings.length; i++) {
//            //该索引处字符串包含.jar
//            if (strings[i].contains(".jar")){
//                if (isFirst){
//                    strings[i] = "agent";
//                    symbolIndex = i;
//                    isFirst = false;
//                }else {
//                    reAppear = true;
//                    jarName = strings[i].substring(0,strings[i].indexOf(".jar"));
//                    strings[i] = "";
//                }
//            }
//            //该索引处字符串不包含.jar
//            else {
//                if (!isFirst){
//                    strings[i] = "";
//                }
//            }
//        }
//        strings[symbolIndex] = reAppear?("jar/"+jarName):"class";
//        for (int i = 0; i < strings.length; i++) {
//            newPath = ("".equals(strings[i]))  ? newPath+strings[i] : newPath + (strings[i]+"/");
//        }
//        return reAppear?newPath.substring(0,newPath.length()-1):newPath;
//    }
//
//    /**
//     * @description  判断classPath路径是否为嵌套jar包  是：false  不是：true
//     * @Param classPath
//     * @Return
//     */
//    public static boolean onlyOne(String classPath) {
//        int count = 0;
//        String[] strings = classPath.split("/");
//        for (String s : strings) {
//            if (s.contains(".jar")){
//                count++;
//            }
//            if (count>1) break;
//        }
//        return count == 1;
//    }
//
//
//
//}
