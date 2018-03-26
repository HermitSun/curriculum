package test;

import java.util.*;
import java.io.*;

public class Curriculum {
	
	private Scanner input=new Scanner(System.in);
	File f=new File("D:/CurriculumSchedule.txt");
	ArrayList<String> curriculumList=new ArrayList<>();
	Map<Integer,String> listMap=new HashMap<>();
	//实例
	public static void main(String[] args) {
		new Curriculum().startAndGetInput();
	}
	//启动
	public void startAndGetInput() {
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		//如果用于存储的文件不存在，则创建
		System.out.println("猪皮课程表V1.0");
		System.out.println("初次使用请输入Help/help查看帮助，输入Q/q退出");
		System.out.println("课程信息请以\"星期四，三、四节，计算与软件工程，仙2-407\"的形式输入");
		while(true) {
			String s=input.nextLine();
			if(s.equals("q")||s.equals("Q")) {
				break;
			}
			String[] commands=s.split(" ");
			if(commands.length==1) {
				String command=commands[0];
				getCommand(command);
			}
			else {
				String command=commands[0];
				String data=commands[1];
				getCommand(command,data);
			}
		}
	}
	//将输入分为两部分（以空格为界），前一部分是指令，后一部分是课程内容
	public void getCommand(String command,String data) {
		switch(command) {
		case"Add":
			addData(data);
			break;
		case"add":
			addData(data);
			break;
		case"Remove":
			removeData(data);
			break;
		case"remove":
			removeData(data);
			break;
		case"Find":
			findData(data);
			break;
		case"find":
			findData(data);
			break;
		}
	}
	public void getCommand(String command) {
		switch(command) {
		case"Update":
			updateData();
			break;
		case"update":
			updateData();
			break;
		case"Show":
			showData_sort();
			showData_print();
			break;
		case"show":
			showData_sort();
			showData_print();
			break;
		case"Help":
			giveHelp();
			break;
		case"help":
			giveHelp();
			break;
		}
	}
	//支持的命令
	public void addData(String data) {
		if(judgeNameRepeat(data,readFile())) {
			System.out.println("课程冲突");
		}
		else {
			writeFile(data,true);
			System.out.println("已添加到文件中");
		}
	}
	//添加，第二个参数表示在末尾追加
	public void removeData(String data) {
		if(!fileIsEmpty()) {
			String[] contents=readFile();
			ArrayList<String> list=new ArrayList<>(Arrays.asList(contents));
			//转换成ArrayList方便处理
			if(list.contains(data)) {
				list.remove(data);
				clearData();
				//清空文件再写入，否则会出现玄学错误（？）
				for(String s:list) {
					writeFile(s,false);
				}
				System.out.println("已从文件删除");
			}
			else {
				System.out.println("课程不存在");
			}
		}
	}
	//删除，第二个参数表示从开头开始
	public void updateData() {
		if(!fileIsEmpty()) {
			System.out.println("现有的课程：");
			showData_sort();
			showData_print();
			
			System.out.print("选择需要改动的课程：");
			int count=input.nextInt();
			String selectedContent=listMap.get(count);
			System.out.println("你选择的是："+selectedContent);
			
			System.out.println("下面请开始你的表演：");
			System.out.println("1->修改日期");
			System.out.println("2->修改节次");
			System.out.println("3->修改名称");
			System.out.println("4->修改地点");
			System.out.println("需要更大幅度修改请移步删除，然后再次添加（你懂我意思吧）：");	
			
			int command=input.nextInt();
			if(command==1) {
				System.out.print("请输入修改后的日期：");
				String s1=input.next();
				updateContent(s1,selectedContent,command);
			}
			else if(command==2) {
				System.out.print("请输入修改后的节次：");
				String s2=input.next();
				updateContent(s2,selectedContent,command);
			}
			else if(command==3) {
				System.out.print("请输入修改后的名称：");
				String s3=input.next();
				updateContent(s3,selectedContent,command);
			}
			else if(command==4) {
				System.out.print("请输入修改后的地点：");
				String s4=input.next();
				updateContent(s4,selectedContent,command);
			}
			System.out.println("已更新文件");
		}
	}
	//更新
	public void updateContent(String s,String selectedContent,int count) {
		String[] contents=selectedContent.split("，");
		contents[count-1]=s;
		String target=contents[0]+"，"+contents[1]+"，"+contents[2]+"，"+contents[3];
		if(!judgeNameRepeat(target,readFile())) {
			listMap.put(count, target);
			String[] contents2=readFile();
			int index=Arrays.binarySearch(contents2, selectedContent);
			contents2[index]=null;
			clearData();
			for(String ss:contents2) {
				writeFile(ss,true);
			}
			writeFile(target,true);
			showData_sort();
		}
		else {
			System.out.println("课程冲突");
		}
	}
	//更新的内置方法
	public void findData(String data) {
		if(!fileIsEmpty()) {
			String[] contents=readFile();
			ArrayList<String> list=new ArrayList<>(Arrays.asList(contents));
			//转换成ArrayList方便处理
			for(String s:list) {
				if(s.startsWith(data)) {
					String[] ss=s.split("，");
					System.out.println(ss[2]+"，"+ss[3]);
					return;
				}
			}
			System.out.println("不存在此课程");
		}
	}
	//搜索
	public void showData_sort() {
		if(!fileIsEmpty()) {
			String[] contents=readFile();
			ArrayList<String> list=new ArrayList<>(Arrays.asList(contents));
			//转换成ArrayList方便处理
			Comparator<String> second=new Comparator<String>() {
				public int compare(String s1, String s2) {
					return compareNumber(s1,s2);
				}
			};
			//比较节数
			ArrayList<String> monday=new ArrayList<>();
			ArrayList<String> tuesday=new ArrayList<>();
			ArrayList<String> wednesday=new ArrayList<>();
			ArrayList<String> thursday=new ArrayList<>();
			ArrayList<String> friday=new ArrayList<>();
			for(String s:list) {
				if(s.startsWith("星期一")) {
					monday.add(s);
				}
				if(s.startsWith("星期二")) {
					tuesday.add(s);
				}
				if(s.startsWith("星期三")) {
					wednesday.add(s);
				}
				if(s.startsWith("星期四")) {
					thursday.add(s);
				}
				if(s.startsWith("星期五")) {
					friday.add(s);
				}
			}
			
			Collections.sort(monday, second);
			Collections.sort(tuesday, second);
			Collections.sort(wednesday, second);
			Collections.sort(thursday, second);
			Collections.sort(friday, second);
			
			for(String s:monday) {
				if(!isArrayListContentRepeat(s,curriculumList)) {
					curriculumList.add(s);
				}
			}
			for(String s:tuesday) {
				if(!isArrayListContentRepeat(s,curriculumList)) {
					curriculumList.add(s);
				}
			}
			for(String s:wednesday) {
				if(!isArrayListContentRepeat(s,curriculumList)) {
					curriculumList.add(s);
				}
			}
			for(String s:thursday) {
				if(!isArrayListContentRepeat(s,curriculumList)) {
					curriculumList.add(s);
				}
			}
			for(String s:friday) {
				if(!isArrayListContentRepeat(s,curriculumList)) {
					curriculumList.add(s);
				}
			}
		}
	}
	//排序
	public void showData_print() {
		showData_sort();
		for(int i=0;i<curriculumList.size();i++) {
			listMap.put(i+1, curriculumList.get(i));
		}
		for(int i=1;i<=listMap.keySet().size();i++) {
			System.out.println(i+":"+listMap.get(i));
		}
	}
	//显示
	public boolean isArrayListContentRepeat(String s,ArrayList<String>curriculumList) {
		if(curriculumList.contains(s)) {
			return true;
		}
		return false;
	}
	//判断ArrayList内容重复
	public void giveHelp() {
		System.out.println("支持的命令：");
		System.out.println("Add/add 日期，节次，名称，教室->添加");
		System.out.println("Remove/remove ->删除");
		System.out.println("Find/find 日期，节次->查询");
		System.out.println("Update/update ->更新");
		System.out.println("Show/show ->显示");
	}
	//帮助
	@SuppressWarnings("resource")
	public String[] readFile() {
		String[] contents = null;
		ArrayList<String> temp=new ArrayList<>();
		try {
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String content=br.readLine();
			if(content==null) {
				return null;
			}
			while(content!=null) {
				temp.add(content);
				content=br.readLine();
			}
			contents=new String[temp.size()];
			contents=temp.toArray(contents);
			br.close();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return contents;
	}
	//读取文件
	public void writeFile(String data,boolean state) {
		try {
			FileWriter fw=new FileWriter(f,state);
			PrintWriter pw=new PrintWriter(fw);
			if(data!="") {
				pw.println(data);
				pw.flush();
			}
			pw.close();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	//写入文件
	public void clearData() {
		try {
			FileWriter fw = new FileWriter(f,false);
			fw.close();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	//清空文件
	public boolean judgeNameRepeat(String data,String[] contents) {
		if(contents==null) {
			return false;
		}
		for(String s:contents) {
			String[] ss=s.split("，");
			String regex=ss[0]+"，"+ss[1]+"，"+"(\\S)+"+"，"+ss[3];
			if(data.matches(regex)) {
				return true;
			}
		}
		return false;
	}
	//判断课程名重复，使用正则表达式
	public boolean fileIsEmpty() {
		if(readFile()==null) {
			System.out.println("暂无课程信息");
			return true;
		}
		return false;
	}
	//判断文件是否为空
	public int compareNumber(String s1,String s2) {
		if(s1.matches("(\\S)+三、四节(\\S)+")&&s2.matches("(\\S)+七、八节(\\S)+")) {
			return -1;
		}
		else if(s1.matches("(\\S)+五、六节(\\S)+")&&s2.matches("(\\S)+七、八节(\\S)+")) {
			return -1;
		}
		else if(s1.matches("(\\S)+五、六节(\\S)+")&&s2.matches("(\\S)+九、十节(\\S)+")) {
			return -1;
		}
		else if(s1.matches("(\\S)+七、八节(\\S)+")&&s2.matches("(\\S)+三、四节(\\S)+")) {
			return 1;
		}
		else if(s1.matches("(\\S)+七、八节(\\S)+")&&s2.matches("(\\S)+五、六节(\\S)+")) {
			return 1;
		}
		else if(s1.matches("(\\S)+九、十节(\\S)+")&&s2.matches("(\\S)+五、六节(\\S)+")) {
			return 1;
		}
		else {
			return s1.compareTo(s2);
		}
	}
	//比较课程节数
}