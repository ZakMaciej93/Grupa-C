package com.kino.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import com.kino.Baa.Bazyy.DAO.Movie;
import com.kino.Baa.Bazyy.DAO.User;
import com.kino.Baa.Bazyy.DAO.Seance;
import com.kino.Baa.Bazyy.connector.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MainController {
	ApplicationContext context = new ClassPathXmlApplicationContext("aa.xml");
	SqliteDAO sqliteDAO = (SqliteDAO) context.getBean("sqliteDAO");

	@RequestMapping(value = "/welcome")
	public ModelAndView displayAll(Map<String, Object> model) {
		model.put("movies",sqliteDAO.getAllMovies());
		String message = "Tre�� newsa ze Springa";
		model.put("message", message);
		return new ModelAndView("welcome", model);
	}

	@RequestMapping("/login")
	public ModelAndView login(Map<String, Object> model) {

		return new ModelAndView("login", model);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String viewRegistration(Map<String, Object> model) {
		User userForm = new User();
		model.put("userForm", userForm);
		return "create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String processRegistration(@ModelAttribute("userForm") User user, Model model) {	
		String result = "createSuccess";
		if (!user.getLogin().isEmpty() && !user.getPhone().isEmpty() && !user.getEmail().isEmpty()) {
			if (sqliteDAO.getUserByLogin(user.getLogin()).isEmpty()) {
				System.out.println("Rejestruj� usera " + user.getLogin());
				sqliteDAO.insertUser(user);
				model.addAttribute("message", user.getLogin());
			} else {
				model.addAttribute("message", "Login jest ju� zaj�ty");
				result = "create";
			}
		} else {
			model.addAttribute("message", "Wype�nij wszystkie pola");
			result = "create";
		}
		return result;
	}
	
//ADMIN
	
	@RequestMapping("/admin/admin_panel")
	public ModelAndView viewAdminPanel(Map<String, Object> model) {
		return new ModelAndView("admin/admin_panel", model);
	}
	
//ADMIN MOVIES
	
	@RequestMapping(value = "/admin/admin_movies", method = RequestMethod.GET)
	public String viewAdminMovies(Map<String, Object> model) {
		
		model.put("movies",sqliteDAO.getAllMovies());
		return "admin/admin_movies";
	}
	
	@RequestMapping(value = "/admin/admin_movies", method = RequestMethod.POST)
	public String adminMovieAction(@RequestParam String action, @RequestParam String title, @ModelAttribute("movieForm") Movie movie, Model model) {
		String result="redirect:/admin/admin_movies.html";
		if(action.equals("Edytuj")){
			result="/admin/admin_movie_edit";
			model.addAttribute("movieForm",sqliteDAO.getMovieListByName(title).get(0));
			model.addAttribute("title",title);
			model.addAttribute("message","Tytu� jest identyfikatorem - zmiana spowoduje dodanie nowego filmu do bazy danych, lub zmian� istniej�cego!");
		}else if(action.equals("Skasuj")){
			sqliteDAO.deleteMovie(title);
		}else if(action.equals("Zapisz zmiany")){
			sqliteDAO.insertOrReplaceMovie(movie);
		}else if(action.equals("Dodaj nowy")){
			result="/admin/admin_movie_edit";
			Movie newMovie=new Movie();
			model.addAttribute("movieForm",newMovie);
			model.addAttribute("title","Nowy film");
		}
		return result;
	}
	
//ADMIN SEANCES
	
	@RequestMapping(value = "/admin/admin_seances", method = RequestMethod.GET)
	public String viewAdminSeances(Map<String, Object> model) {
		model.put("seances",sqliteDAO.getAllSeances());
		return "admin/admin_seances";
	}
	
	@RequestMapping(value = "/admin/admin_seances", method = RequestMethod.POST)
	public String adminSeanceAction(@RequestParam String action, @RequestParam String ID, @ModelAttribute("seanceForm") Seance seance, Model model) {
		String result="redirect:/admin/admin_seances.html";
		if(action.equals("Edytuj")){
			result="/admin/admin_seance_edit";
			model.addAttribute("movies",sqliteDAO.getAllMovies());
			model.addAttribute("seanceForm",sqliteDAO.getSeanceByID(ID).get(0));
			model.addAttribute("ID",ID);
			model.addAttribute("message","ID jest identyfikatorem - zmiana spowoduje dodanie nowego seansu do bazy danych, lub zmian� istniej�cego!");
		}else if(action.equals("Skasuj")){
			sqliteDAO.deleteSeance(ID);
		}else if(action.equals("Zapisz zmiany")){
			sqliteDAO.insertOrReplaceSeance(seance);
		}else if(action.equals("Dodaj nowy")){
			model.addAttribute("movies",sqliteDAO.getAllMovies());
			result="/admin/admin_seance_edit";
			Seance newSeance=new Seance();
			model.addAttribute("seanceForm",newSeance);
			model.addAttribute("ID","Nowy seans");
		}
		return result;
	}
	
//ADMIN_USERS
	
	@RequestMapping(value = "/admin/admin_users", method = RequestMethod.GET)
	public String viewAdminUsers(Map<String, Object> model) {
		model.put("users",sqliteDAO.getAllUsers());
		return "admin/admin_users";
	}
	
	@RequestMapping(value = "/admin/admin_users", method = RequestMethod.POST)
	public String adminUserAction(@RequestParam String action, @RequestParam String login, @ModelAttribute("userForm") User user, Model model) {
		String result="redirect:/admin/admin_users.html";
		if(action.equals("Edytuj")){
			result="/admin/admin_user_edit";
			model.addAttribute("userForm",sqliteDAO.getUserByLogin(login).get(0));
			model.addAttribute("login",login);
			model.addAttribute("message","Login jest identyfikatorem - zmiana spowoduje dodanie nowego u�ytkownika do bazy danych, lub zmian� istniej�cego!");
		}else if(action.equals("Skasuj")){
			sqliteDAO.deleteUser(login);
		}else if(action.equals("Zapisz zmiany")){		
			sqliteDAO.insertOrReplaceUser(user);
		}else if(action.equals("Dodaj nowy")){
			result="/admin/admin_user_edit";
			User newUser=new User();
			model.addAttribute("userForm",newUser);
			model.addAttribute("login","Nowy u�ytkownik");
		}
		return result;
	}
	
}
