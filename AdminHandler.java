package net.fuzui.StudentInfo.handler;

import net.fuzui.StudentInfo.pojo.Student;
import net.fuzui.StudentInfo.pojo.Teacher;
import net.fuzui.StudentInfo.service.AdminService;
import net.fuzui.StudentInfo.service.CoursePlanService;
import net.fuzui.StudentInfo.service.CourseService;
import net.fuzui.StudentInfo.service.SelectCourseService;
import net.fuzui.StudentInfo.service.StudentService;
import net.fuzui.StudentInfo.service.TeacherService;
import net.fuzui.StudentInfo.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/AdminHandler")
//@SessionAttributes("studentList")
//@SessionAttributes("teacherList")
public class AdminHandler {


    @Autowired
	StudentService studentService;
	@Autowired
	TeacherService teacherService;
	@Autowired
	CoursePlanService coursePlanService;
	@Autowired
    CourseService courseService;
	@Autowired
	SelectCourseService selectCourseService;


	/**
	 * JavaScript学的不好，目前只能通过这种方式把三级联动下拉列表的val改变为以下值传给数据库。
	 * 
	 */
	String[] arr_belongcoll = {"大数据与软件工程学院", "设计艺术与建筑学院", "外语学院", "法学院"};
	String[][] arr_belongpro = {
                    {"计算机科学与技术", "软件工程", "统计学", "计算机科学与技术（专升本）"},
                    {"视觉传达设计", "环境设计", "产品设计", "建筑学"},
                    {"英语", "日语", "商务英语"},
                    {"法学", "电子商务及法律", "社会工作", "公共事业管理"}
				};
	String[][][] arr_belongcla = {
                {
                    {"计算机191","计算机192","计算机193"},
                    {"软件191","软件192","软件193"},
                    {"统计191","统计192","统计193"},
                    {"计算机1901","计算机1902"}
                    
                },
                {
                    {"视觉191","视觉192","视觉193"},
                    {"环境191","环境192","环境193"},
                    {"产品191","产品192","产品193"},
                    {"建筑191","建筑192","建筑193"}
                    
                },
                {
                    {"英语191","英语192","英语193"},
                    {"日语191","日语192","日语193"},
                    {"商务英语191","商务英语192","商务英语193"}
                    
				},
                {
                    {"法学191","法学192","法学193"},
                    {"电子商务191","电子商务192","电子商务193"},
                    {"社会工作191","社会工作192","社会工作193"},
                    {"公共事业191","公共事业192","公共事业193"}
                    
				}
		};

 // 添加
 	@RequestMapping("/addStudent")
 	public String addStudent(Student student, Model model) {

 		int col = Integer.parseInt(student.getCollege());
 		int pro = Integer.parseInt(student.getProfession());
 		int cla = Integer.parseInt(student.getClassr());

 		student.setCollege(arr_belongcoll[col]);
 		student.setProfession(arr_belongpro[col][pro]);
 		student.setClassr(arr_belongcla[col][pro][cla]);

 		if (studentService.insertStudent(student) != 0) {
 			model.addAttribute("student", student);
 			return "success";
 			// return "admin/addStudent";
 		} else {
 			return "fail";
 		}

 	}
 	//查询全部学生方法
 	public void queryStu(HttpServletRequest request) {
 		List<Student> studentList = new ArrayList<Student>();
 		studentList = studentService.selectStudentBySql(1,10);
 		
 		
 		request.setAttribute("slist", studentList);
 	}

 	public void pageIn(Model model,List list) {
 		PageInfo page = new PageInfo(list, 5);
	 	model.addAttribute("pageInfo", page);
 	}
 	
 	// 查询
 	@RequestMapping(value = "/query/{pn}", method = RequestMethod.GET)
 	public String redirect(@RequestParam("serc") String serc, @RequestParam("condition") String condition,HttpServletRequest request,
 			@PathVariable(value = "pn") String pn,Model model) {

 		int no = Integer.parseInt(pn);
 		List<Student> studentList = new ArrayList<Student>();
 		PageHelper.startPage(no, 5);
 		request.setAttribute("serc", serc);
		request.setAttribute("condition", condition);
 		//查询全部
 		if (serc.equals("all")) {
 			System.out.println("------------------------------------------------------------------------------------------------");
 			studentList = studentService.selectStudentBySql(1,10);
 			pageIn(model, studentList);
 	 		request.setAttribute("slist", studentList);
 			return "admin/queryStudent";

 		//根据学号查询
 		} else if (serc.equals("sid")) {

 			studentList = studentService.getByStudentSid(1,10,condition);
 			pageIn(model, studentList);
 			request.setAttribute("slist", studentList);
 			System.out.println("sid");

 			return "admin/queryStudent";

 		//根据学院查询
 		} else if (serc.equals("col")) {
 			
 			
 			studentList = studentService.getByStudentCol(1,10,condition);
 			pageIn(model, studentList);
 			request.setAttribute("slist", studentList);
 			System.out.println(studentList);
 			System.out.println("col");
 			return "admin/queryStudent";

 		//根据专业查询
 		} else if (serc.equals("pro")) {
 			studentList = studentService.getByStudentPro(1,10,condition);
 			pageIn(model, studentList);
 			request.setAttribute("slist", studentList);
 			System.out.println(studentList);
 			System.out.println("pro");
 			return "admin/queryStudent";

 		//根据班级查询
 		} else if (serc.equals("cla")) {
 			studentList = studentService.getByStudentCla(1,10,condition);
 			pageIn(model, studentList);
 			request.setAttribute("slist", studentList);
 			return "admin/queryStudent";

 		} else {

 			studentList = studentService.selectStudentBySql(1,10);
 			pageIn(model, studentList);
 	 		request.setAttribute("slist", studentList);
 			return "admin/queryStudent";

 		}

 	}

 	// 删除学生
 	@RequestMapping(value = "/delete/{sid}", method = RequestMethod.GET)
 	public String deleteStudent(@PathVariable(value = "sid") String sid, Model model) {

 		if (studentService.deleteStudent(sid) != 0) {
 			System.out.println("success");
 			
 			return "success";
 		} else {
 			System.out.println("fail");
 			return "fail";
 		}

 	}

 	// 跳转页面
 	@RequestMapping(value = "/finalPage", method = RequestMethod.GET)
 	public String finalPage(HttpServletRequest request) {
 		queryStu(request);
 		return "admin/queryStudent";
 	}

 	// 修改定位
 	@RequestMapping(value = "/moditystu/{sid}", method = RequestMethod.GET)
 	public String editPre(@PathVariable("sid") String sid, HttpServletRequest request) {

 		List<Student> studentList = new ArrayList<Student>();
 		studentList = studentService.getByStudentSid(1,10,sid);
 		
 		request.setAttribute("studentList", studentList);
 		System.out.println("-----进入修改");
 		return "admin/modiStudent";
 	}

 	
 	
 	// 修改
 	@RequestMapping(value = "/moditystud/{sid}", method = RequestMethod.GET)
 	public String update(@PathVariable("sid") String sid, Student student, HttpServletRequest request) {

 		int col = Integer.parseInt(student.getCollege());
 		int pro = Integer.parseInt(student.getProfession());
 		int cla = Integer.parseInt(student.getClassr());
 		student.setCollege(arr_belongcoll[col]);
 		student.setProfession(arr_belongpro[col][pro]);
 		student.setClassr(arr_belongcla[col][pro][cla]);
 		
 		if (studentService.modifyStudent(student) != 0) {
 			System.out.println("----修改成功--------------------------------------------------------------------------------------------------------");
 			return "success";
 		} else {
 			System.out.println("----修改失败----------------------------------------------------------------");
 			return "fail";
 		}
 	}
 	
 	
 	

 	// 跳转页面
 	@RequestMapping("/managestu/{pn}")
 	public String manageStudent(HttpServletRequest request,
 			@PathVariable(value = "pn") String pn,Model model) {
 		int no = Integer.parseInt(pn);
 		
 		PageHelper.startPage(no, 5);
 		List<Student> studentList = new ArrayList<Student>();
 		studentList = studentService.selectStudentBySql(1,100);
 		pageIn(model, studentList);
 		request.setAttribute("slist", studentList);
 		return "admin/queryStudent";
 	}
 		
 	// 跳转页面
 	@RequestMapping("/managetea/{pn}")
 	public String manageTeacher(HttpServletRequest request,
 			@PathVariable(value = "pn") String pn,Model model) {
 		int no = Integer.parseInt(pn);
 		PageHelper.startPage(no, 5);
 		List<Teacher> teacherList = new ArrayList<Teacher>();
	 	teacherList = teacherService.selectTeacherBySql(1,10);
	 	pageIn(model, teacherList);
	 	request.setAttribute("teacherList", teacherList);
 		return "admin/queryTeacher";
 	}


 	// 跳转页面
 	@RequestMapping("/addstu")
 	public String adStudent() {
 		return "admin/addStudent";
 	}

 	// 跳转页面
 	@RequestMapping("/addtea")
 	public String adTeacher() {
 		return "admin/addTeacher";
 	}

 	// 跳转页面
 	@RequestMapping("/addcou")
 	public String adCourse() {
 		return "admin/addCourse";
 	}
 	
 // 添加
 	@RequestMapping("/addTeacher")
 	public String addTeacher(Teacher teacher, Model model, HttpSession httpSession) {

 		if (teacherService.insertTeacher(teacher) != 0) {
 			model.addAttribute("teacher", teacher);
 			return "success";
 		} else {
 			return "fail";
 		}

 	}

 	
 	/**
 	 * 教师相关
 	 */
 	
 	//查询全部教师方法
 	 	public void queryTea(HttpServletRequest request) {
 	 		List<Teacher> teacherList = new ArrayList<Teacher>();
 	 		teacherList = teacherService.selectTeacherBySql(1,10);
 	 		request.setAttribute("teacherList", teacherList);
 	 	}
 	
 	// 查询
 	@RequestMapping(value = "/queryTea/{pn}", method = RequestMethod.GET)
 	public String redirectTea(@RequestParam("serc") String serc, @RequestParam("condition") String condition,HttpServletRequest request,
 			@PathVariable(value = "pn") String pn,Model model) {
 		int no = Integer.parseInt(pn);
 		PageHelper.startPage(no, 5);
 		List<Teacher> teacherList = new ArrayList<Teacher>();
 		request.setAttribute("serc", serc);
		request.setAttribute("condition", condition);
 		
 		if (serc.equals("all")) {

 			teacherList = teacherService.selectTeacherBySql(1,10);
 			pageIn(model, teacherList);
 	 		request.setAttribute("teacherList", teacherList);
 			return "admin/queryTeacher";

 		} else if (serc.equals("tid")) {

 			teacherList = teacherService.getByTeacherTid(1,10,condition);
 			pageIn(model, teacherList);
 			request.setAttribute("teacherList", teacherList);
 			System.out.println("tid");

 			return "admin/queryTeacher";

 		} else {

 			teacherList = teacherService.selectTeacherBySql(1,10);
 			pageIn(model, teacherList);
 	 		request.setAttribute("teacherList", teacherList);
 			return "admin/queryTeacher";

 		}

 	}
 	
 	

 	//删除教师
 	@RequestMapping(value = "/deleteTea/{tid}", method = RequestMethod.GET)
 	public String deleteTeacher(@PathVariable(value = "tid") String tid, Model model) {


 		if (teacherService.deleteTeacher(tid) != 0) {
 			System.out.println("success");
 			return "success";
 		} else {
 			System.out.println("fail");
 			return "fail";
 		}

 	}

 	@RequestMapping(value = "/finalPageTea", method = RequestMethod.GET)
 	public String finalPageTea(HttpServletRequest request) {
 		queryTea(request);
 		return "admin/queryTeacher";
 	}

 	//修改定位
 	@RequestMapping(value = "/modityTea/{tid}", method = RequestMethod.GET)
 	public String editPreTea(@PathVariable("tid") String tid, HttpServletRequest request) {

 		List<Teacher> teacherList = new ArrayList<Teacher>();
 		teacherList = teacherService.getByTeacherTid(1,10,tid);
 		request.setAttribute("teacherList", teacherList);

 		return "admin/modiTeacher";
 	}

 	// 修改
 	@RequestMapping(value = "/modityTeac/{tid}", method = RequestMethod.GET)
 	public String update(@PathVariable("tid") String tid, Teacher teacher, HttpServletRequest request) {

 		if (teacherService.modifyTeacher(teacher) != 0) {
 			return "success";
 		} else {
 			return "fail";
 		}
 	}
}
